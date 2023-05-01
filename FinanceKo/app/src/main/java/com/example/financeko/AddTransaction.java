package com.example.financeko;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {
    DBHelper DB;
    private EditText amount;
    private Button backBtn, addTransactionBtn;

    DatePickerDialog datePickerDialog;
    private TextView date_textview;

    static String[] Categories = {"Transportation", "Bills", "Necessity", "Food", "Others"};
    static String[] Frequency = {"Once", "Daily", "Monthly", "Yearly"};
    AutoCompleteTextView categoryDropDown, frequencyDropDown;
    ArrayAdapter<String> adapterItemCategory, adapterItemFrequency;

    //Values to be inserted on add transaction
    String categoryInput = null, frequencyInput = null;
    String selectedItemCategory, selectedItemFrequency, selectedDate;

    //Set up date and time



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        DB = new DBHelper(this);
        amount = findViewById(R.id.amount_edittext);
        backBtn = findViewById(R.id.backBtn);
        addTransactionBtn = findViewById(R.id.addTransactionBtn);
        date_textview = findViewById(R.id.date_textview);
        categoryDropDown = findViewById(R.id.category_dropdown);
        frequencyDropDown = findViewById(R.id.frequency_dropdown);

        //Set default values
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        selectedDate = currentDate;
        selectedItemCategory = Categories[0];
        selectedItemFrequency = Frequency[0];

        date_textview.setText(currentDate);
        frequencyDropDown.setText(Frequency[0], false);
        categoryDropDown.setText(selectedItemCategory, false);

        //Dropdown Menu CATEGORY
        adapterItemCategory = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, Categories);
        categoryDropDown.setAdapter(adapterItemCategory);
        categoryDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItemCategory = adapterView.getItemAtPosition(i).toString();
            }
        });


        //Dropdown Menu FREQUENCY
        adapterItemFrequency = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, Frequency);
        frequencyDropDown.setAdapter(adapterItemFrequency);
        frequencyDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItemFrequency = adapterView.getItemAtPosition(i).toString();
            }
        });

        date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("date_edittext", "date_edittext has been clicked");
                // Get the current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new DatePickerDialog and show it
                datePickerDialog = new DatePickerDialog(AddTransaction.this, R.style.MyDatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Set the selected date on the EditText
                                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                                selectedDate = formattedDate;
                                date_textview.setText(formattedDate);
                                datePickerDialog.dismiss();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        //Add Transaction
        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int number = 0;
                boolean isValidAmount = true;

                categoryInput = selectedItemCategory;
                frequencyInput = selectedItemFrequency;
                String amountInput = amount.getText().toString().trim();
                String currentDate = null;

                //Convert to amountInput to int
                try{
                    number = Integer.parseInt(amountInput);
                }catch (NumberFormatException e){
                    Toast.makeText(AddTransaction.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }

                if(amountInput.equals("")){
                    Toast.makeText(AddTransaction.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(isValidAmount){
                        //Get current login-ed user
                        String currentUser = MainActivity.loginUser;

                        //insert data transaction
                        Boolean insert = DB.insertDataTransaction(currentUser, selectedDate, categoryInput,frequencyInput, number);
                        if(insert){
                            Toast.makeText(AddTransaction.this, "Transaction Added", Toast.LENGTH_SHORT).show();
                            Log.e("insertDataTransaction", "Transaction Added: " +
                                    "\nUser: " + currentUser +
                                    "\nDate: " + selectedDate +
                                    "\nCategory: " + categoryInput +
                                    "\nFrequency: " + frequencyInput +
                                    "\nAmount: " + number);
                        }
                        else
                            Toast.makeText(AddTransaction.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTransaction.this,HomePageActivity.class);
                startActivity(intent);
                AddTransaction.this.finish();
            }
        });
    }
}