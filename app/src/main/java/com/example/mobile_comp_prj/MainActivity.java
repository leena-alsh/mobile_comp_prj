
package com.example.mobile_comp_prj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.loginButton);
        Button createAccButton = findViewById(R.id.createAccButton);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, loginActivity.class);
            startActivity(intent);
        });

        createAccButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccActivity.class);
            startActivity(intent);
        });
    }
}
