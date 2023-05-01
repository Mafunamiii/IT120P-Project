package com.example.financeko;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

import java.util.Calendar;

/*
    Features to be desired:
    Database Encryption - SQLite does not have encryption, apparently needs some add-ons sheesh
    Cloud database synchronization - something like firebase to store user data online
    Responsive design - sheesh very sheesh

    Archive transactions based on frequency -
        Remove all "Once" transactions from transaction history and store them possibly in another table
        This will reduce the clutter on transaction history

    More Summary details
        -Show spending each month
        -Average spending metrics to get most and least spent category on average
        -Pie chart because pie is good

     */
public class MainActivity extends AppCompatActivity {


    public static String loginUser;//The currently login-ed user
    EditText username_edittext, password_edittext;
    Button login_button, register_button;
    DBHelper DB;

    //Set calendar
    static Calendar calendar = Calendar.getInstance();
    static int year = calendar.get(Calendar.YEAR);
    static int month = calendar.get(Calendar.MONTH) + 1; // Note: Month value is 0-based, so add 1 to get the actual month value
    static int day = calendar.get(Calendar.DAY_OF_MONTH);
    static String currentDate = year + "-" + month + "-" + day; // Format the date as a string (YYYY-MM-DD)


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

        //Set alarm manager for transactions with frequencies
        //Alarm to add transaction with frequencies
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, 0);
        alarmCalendar.set(Calendar.MINUTE, 0);
        alarmCalendar.set(Calendar.SECOND, 0);

        // Schedule a repeating alarm that goes off at midnight every day
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


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
