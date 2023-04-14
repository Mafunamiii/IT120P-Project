package com.example.financeko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {
    DBHelper DB;
    EditText category, frequency, amount;
    Button backBtn, addTransactionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        DB = new DBHelper(this);

        category = findViewById(R.id.category_edittext);
        frequency = findViewById(R.id.frequency_edittext);
        amount = findViewById(R.id.amount_edittext);
        backBtn = findViewById(R.id.backBtn);
        addTransactionBtn = findViewById(R.id.addTransactionBtn);


        //Add Transaction
        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int number = 0;
                boolean isValidAmount = true;

                String categoryInput = category.getText().toString().trim();
                String frequencyInput = frequency.getText().toString().trim();
                String amountInput = amount.getText().toString().trim();

                //Convert to int
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
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        //insert data transaction
                        Boolean insert = DB.insertDataTransaction("Jahn", "2023-04-14", "None","None", 2342);
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