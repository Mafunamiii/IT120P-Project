package com.example.financeko;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTransaction extends AppCompatActivity {

    DBHelper DB;
    private EditText amount;
    private TextView date_textview;
    private Button backBtn, applyChangesBtn;

    private DatePickerDialog datePickerDialog;

    String currentDate = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date());
    String[] Frequency = {"Once", "Daily", "Monthly", "Yearly"};
    AutoCompleteTextView categoryDropDown, frequencyDropDown;
    ArrayAdapter<String> adapterItemCategory, adapterItemFrequency;

    //Values to be inserted on add transaction
    String categoryInput = null, frequencyInput = null, amountInput;
    String selectedItemCategory, selectedItemFrequency, selectedDate;

    //Get transaction to be edited
    TransactionModel transactionModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        date_textview = findViewById(R.id.date_textview);
        amount = findViewById(R.id.amount_edittext);
        backBtn = findViewById(R.id.backBtn);
        applyChangesBtn = findViewById(R.id.applyChangesBtn);
        DB = new DBHelper(this);

        //Dropdown Menu CATEGORY
        categoryDropDown = findViewById(R.id.category_dropdown);
        adapterItemCategory = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, AddTransaction.Categories);
        categoryDropDown.setAdapter(adapterItemCategory);
        categoryDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItemCategory = adapterView.getItemAtPosition(i).toString();
            }
        });


        //Dropdown Menu FREQUENCY
        frequencyDropDown = findViewById(R.id.frequency_dropdown);
        adapterItemFrequency = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, Frequency);
        frequencyDropDown.setAdapter(adapterItemFrequency);
        frequencyDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItemFrequency = adapterView.getItemAtPosition(i).toString();
            }
        });

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("EditTransaction", "\nCategory: "+ selectedItemCategory + 
                                                    "\nFrequency: " + selectedItemFrequency + 
                                                    "\nAmount: " + Integer.parseInt(amountInput) +
                                                    "\nTransactionID: " + transactionModel.getId());
                Boolean update = DB.updateDataTransaction(transactionModel.getId(), currentDate, selectedItemCategory, selectedItemFrequency, Integer.parseInt(amount.getText().toString()));
                if(update){
                    Toast.makeText(EditTransaction.this, "Update successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditTransaction.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                }
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
                datePickerDialog = new DatePickerDialog(EditTransaction.this, R.style.MyDatePickerTheme,
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTransaction.this,TransactionHistory.class);
                startActivity(intent);
                EditTransaction.this.finish();
            }
        });

        // Retrieve the intent and get the transaction model
        Intent intent = getIntent();
        if (intent != null) {
            transactionModel = getIntent().getParcelableExtra("transactionModel");
            //Set Data
            if (transactionModel != null) {
                categoryDropDown.setText(transactionModel.getCategory(), false);
                frequencyDropDown.setText(transactionModel.getFrequency(), false);
                amount.setText(transactionModel.getAmount());
                date_textview.setText((transactionModel.getDate()));

                //initialize selected items
                selectedItemCategory = transactionModel.getCategory();
                selectedItemFrequency = transactionModel.getFrequency();
                amountInput = transactionModel.getAmount();
                selectedDate = transactionModel.getDate();
            }
        }

        //get values
        amountInput = amount.getText().toString();
        Log.e("EditTransaction.java", amountInput);
    }
}
