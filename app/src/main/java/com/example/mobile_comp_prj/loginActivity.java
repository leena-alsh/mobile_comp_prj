
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

public class loginActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;

    Button loginSubmitButton;
    TextView createAccountText;

    database databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginSubmitButton = findViewById(R.id.loginSubmitButton);
        createAccountText = findViewById(R.id.createAccountText);

        databaseHelper = new database(this);

        // Login button
        loginSubmitButton.setOnClickListener(v -> loginUser());

        // Create account link
        createAccountText.setOnClickListener(v -> {
            Intent intent = new Intent(
                    loginActivity.this,
                    AccActivity.class);

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, R.string.login_empty_fields,
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(this, R.string.invalid_email,
                    Toast.LENGTH_SHORT).show();

            return;
        }

        boolean isValid = databaseHelper.checkUser(email, password);

        if (isValid) {

            Toast.makeText(this, R.string.login_success,
                    Toast.LENGTH_SHORT).show();

            // Open home page and pass the user's email
            Intent intent = new Intent(
                    loginActivity.this,
                    homepg.class);

            intent.putExtra("USER_EMAIL", email);

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(this, R.string.login_invalid_credentials,
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
