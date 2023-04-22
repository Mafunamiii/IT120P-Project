package com.example.financeko;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditTransaction extends AppCompatActivity {

    DBHelper DB = new DBHelper(this);
    EditText amount;
    Button backBtn, editTransactionBtn, applyChangesBtn;

    String[] Categories = {"Transportation", "Bills", "Necessity", "Food", "Others"};
    String[] Frequency = {"Once", "Daily", "Monthly", "Yearly"};
    AutoCompleteTextView categoryDropDown, frequencyDropDown;
    ArrayAdapter<String> adapterItemCategory, adapterItemFrequency;

    //Values to be inserted on add transaction
    String categoryInput = null, frequencyInput = null, amountInput;
    String selectedItemCategory = null, selectedItemFrequency = null;

    //Get transaction to be edited
    TransactionModel transactionModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);


        amount = findViewById(R.id.amount_edittext);
        backBtn = findViewById(R.id.backBtn);
        applyChangesBtn = findViewById(R.id.applyChangesBtn);

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


        //get values
        amountInput = amount.getText().toString();
        Log.e("EditTransaction.java", amountInput);

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB.updateDataTransaction(selectedItemCategory, selectedItemFrequency, Integer.parseInt(amountInput), transactionModel.getId());
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

        // Retrieve the intent and get the transaction model DOES NOT WORK FOR SOME REASON
        Intent intent = getIntent();
        if (intent != null) {
            transactionModel = getIntent().getParcelableExtra("transactionModel");
            //Set Data
            if (transactionModel != null) {
                categoryDropDown.setText(transactionModel.getCategory());
                frequencyDropDown.setText(transactionModel.getFrequency());
                amount.setText(transactionModel.getAmount());

            }
        }
    }
}
