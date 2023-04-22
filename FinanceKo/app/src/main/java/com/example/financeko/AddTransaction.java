package com.example.financeko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {
    DBHelper DB;
    EditText amount;
    Button backBtn, addTransactionBtn;

    String[] Categories = {"Transportation", "Bills", "Necessity", "Food", "Others"};
    String[] Frequency = {"Once", "Daily", "Monthly", "Yearly"};
    AutoCompleteTextView categoryDropDown, frequencyDropDown;
    ArrayAdapter<String> adapterItemCategory, adapterItemFrequency;

    //Values to be inserted on add transaction
    String categoryInput = null, frequencyInput = null;
    String selectedItemCategory = null, selectedItemFrequency = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        DB = new DBHelper(this);

        amount = findViewById(R.id.amount_edittext);
        backBtn = findViewById(R.id.backBtn);
        addTransactionBtn = findViewById(R.id.addTransactionBtn);



        //Dropdown Menu CATEGORY
        categoryDropDown = findViewById(R.id.category_dropdown);
        adapterItemCategory = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, Categories);
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


        //Add Transaction
        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int number = 0;
                boolean isValidAmount = true;


                categoryInput = selectedItemCategory;
                frequencyInput = selectedItemFrequency;
                String amountInput = amount.getText().toString().trim();
                String currentDate = null; // NOT YET IMPLEMENTED

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
                        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        //insert data transaction
                        Boolean insert = DB.insertDataTransaction(currentUser, "2023-04-14", categoryInput,frequencyInput, number);
                        if(insert)
                            Toast.makeText(AddTransaction.this, "Transaction Added", Toast.LENGTH_SHORT).show();
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