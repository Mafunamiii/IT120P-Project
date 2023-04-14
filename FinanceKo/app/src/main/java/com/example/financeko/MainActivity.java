package com.example.financeko;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    public static String loginUser;//The currently login-ed user
    EditText username_edittext, password_edittext;
    Button login_button, register_button;
    DBHelper DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        username_edittext = findViewById(R.id.username_edittext);
        password_edittext = findViewById(R.id.password_edittext);
        login_button = findViewById(R.id.login_button);
        register_button = findViewById(R.id.register_button);
        DB = new DBHelper(this);


        // Set onClickListener for login button
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username and password from EditTexts
                String username = username_edittext.getText().toString().trim();
                String password = password_edittext.getText().toString().trim();
                if(username.equals("")||password.equals("")){
                    Toast.makeText(MainActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
                // Check if the username and password are valid
                else{
                    Boolean isUserValid = DB.validateUser(username,password);
                    if(!isUserValid){
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loginUser = username;
                        int userID = DB.getUserId(username);
                        String userString = String.valueOf(userID);

                        Log.i("MainActivity", "The userID of " + loginUser + " is :" + userString);
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);
                    }
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

}
