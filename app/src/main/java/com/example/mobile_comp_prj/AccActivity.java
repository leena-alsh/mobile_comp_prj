
package com.example.mobile_comp_prj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccActivity extends AppCompatActivity {

    EditText nameInput;
    EditText registerEmailInput;
    EditText registerPasswordInput;
    EditText confirmPasswordInput;

    Button registerButton;
    TextView backToLoginText;

    database databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_createacc);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        nameInput = findViewById(R.id.nameInput);
        registerEmailInput = findViewById(R.id.registerEmailInput);
        registerPasswordInput = findViewById(R.id.registerPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        registerButton = findViewById(R.id.registerButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        // connect to the database
        databaseHelper = new database(this);

        // register button
        registerButton.setOnClickListener(v -> registerAccount());

        // back to login
        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(AccActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerAccount() {

        String name = nameInput.getText().toString().trim();
        String email = registerEmailInput.getText().toString().trim();
        String password = registerPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (name.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {

            Toast.makeText(this, R.string.registration_empty_fields,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.invalid_email,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, R.string.password_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.passwords_do_not_match,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // save the account in SQLite
        long result = databaseHelper.registerUser(name, email, password);

        if (result != -1 && result != -2) {

            Toast.makeText(this, R.string.account_created,
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AccActivity.this, loginActivity.class);
            startActivity(intent);
            finish();

        } else if (result == -1) {

            Toast.makeText(this, R.string.email_already_registered,
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, R.string.registration_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
