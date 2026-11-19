
package com.example.mobile_comp_prj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BitsAndBobs.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";

    private static final String TAG = "BitsAndBobsDB";

    public database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUsersTable(db);
        createProductsTable(db);
    }

    private void createUsersTable(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                "salt TEXT NOT NULL, " +
                "password_hash TEXT NOT NULL)";

        db.execSQL(createTable);
    }

    private void createProductsTable(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                "description TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price TEXT NOT NULL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            createProductsTable(db);
        }
    }

    // REGISTER USER
    public long registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            String saltText = toHex(salt);
            String passwordHash = hashPassword(password, salt);

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("email", email);
            values.put("salt", saltText);
            values.put("password_hash", passwordHash);

            return db.insertWithOnConflict(
                    TABLE_USERS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );

        } catch (Exception e) {
            Log.e(TAG, "Registration failed", e);
            return -2;
        }
    }

    // CHECK USER LOGIN
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{"salt", "password_hash"},
                "email = ?",
                new String[]{email},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                String saltText = cursor.getString(0);
                String savedPasswordHash = cursor.getString(1);

                byte[] salt = fromHex(saltText);
                String enteredPasswordHash = hashPassword(password, salt);

                return MessageDigest.isEqual(
                        savedPasswordHash.getBytes(
                                java.nio.charset.StandardCharsets.UTF_8),
                        enteredPasswordHash.getBytes(
                                java.nio.charset.StandardCharsets.UTF_8)
                );
            }

        } catch (Exception e) {
            Log.e(TAG, "Login verification failed", e);
        }

        return false;
    }

    // INSERT PRODUCT
    public long insertProduct(String name, String description,
                              String category, String price) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("category", category);
        values.put("price", price);

        return db.insertWithOnConflict(
                TABLE_PRODUCTS,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    // SELECT ALL PRODUCTS
    public ArrayList<product> getAllProducts() {

        ArrayList<product> productList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_PRODUCTS,
                null,
                null,
                null,
                null,
                null,
                "name ASC"
        )) {

            while (cursor.moveToNext()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("description"));

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category"));

                String price = cursor.getString(
                        cursor.getColumnIndexOrThrow("price"));

                productList.add(new product(
                        name, description, category, price
                ));
            }
        }

        return productList;
    }

    // SEARCH PRODUCTS
    public ArrayList<product> searchProducts(String searchText) {

        ArrayList<product> productList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String searchValue = "%" + searchText.trim() + "%";

        try (Cursor cursor = db.query(
                TABLE_PRODUCTS,
                null,
                "name LIKE ? OR description LIKE ? OR category LIKE ?",
                new String[]{searchValue, searchValue, searchValue},
                null,
                null,
                "name ASC"
        )) {

            while (cursor.moveToNext()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("description"));

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category"));

                String price = cursor.getString(
                        cursor.getColumnIndexOrThrow("price"));

                productList.add(new product(
                        name, description, category, price
                ));
            }
        }

        return productList;
    }

    // DELETE PRODUCT
    public int deleteProduct(String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                TABLE_PRODUCTS,
                "name = ?",
                new String[]{name}
        );
    }

    // PASSWORD HASHING
    private String hashPassword(String password, byte[] salt)
            throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                120000,
                256
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            SecretKey key = factory.generateSecret(spec);
            return toHex(key.getEncoded());

        } finally {
            spec.clearPassword();
        }
    }

    private String toHex(byte[] bytes) {

        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b & 0xff));
        }

        return result.toString();
    }

    private byte[] fromHex(String hex) {

        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;

            bytes[i] = (byte) Integer.parseInt(
                    hex.substring(index, index + 2), 16);
        }

        return bytes;
    }
}