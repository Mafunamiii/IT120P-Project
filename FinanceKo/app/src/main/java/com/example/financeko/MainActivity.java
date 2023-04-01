package com.example.financeko;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    EditText username_edittext, password_edittext;
    Button login_button, register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        username_edittext = findViewById(R.id.username_edittext);
        password_edittext = findViewById(R.id.password_edittext);
        login_button = findViewById(R.id.login_button);
        register_button = findViewById(R.id.register_button);

        // Set onClickListener for login button
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username and password from EditTexts
                String username = username_edittext.getText().toString().trim();
                String password = password_edittext.getText().toString().trim();

                // Check if the username and password are valid
                if (isValidCredentials(username, password)) {
                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                    Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
               startActivity(intent);
           }
        });
    }

    // Validate the user's credentials
    private boolean isValidCredentials(String username, String password) {

        String validUsername = "u";
        String validPassword = "p";

        return username.equals(validUsername) && password.equals(validPassword);
    }
}
