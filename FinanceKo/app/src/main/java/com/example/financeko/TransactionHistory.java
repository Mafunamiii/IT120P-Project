package com.example.financeko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private Button backBtn, ViewHistoryBtn;
    DBHelper DB;
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    AutoCompleteTextView frequencyDropdown, cateogryDropdown;
    ArrayAdapter<String> adapterItemFrequency, adapterItemCategory;

    String[] frequencyOptions = {"Once", "Daily", "Monthly", "Yearly", "All"}; //Added "All"
    String[] cateogryOptions = {"Transportation", "Bills", "Necessity", "Food", "Others", "All"};//Added all

    String selectedFrequency, selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        backBtn = findViewById(R.id.backBtn);

        selectedFrequency = "All";
        selectedCategory = "All";

        //ViewHistoryBtn = findViewById(R.id.ViewHistoryBtn);
        DB = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        setRecyclerView(selectedFrequency, selectedCategory);

        //Dropdown Menu FREQUENCY
        frequencyDropdown = findViewById(R.id.frequency_dropdown_autocomplete);
        adapterItemFrequency = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, frequencyOptions);
        frequencyDropdown.setAdapter(adapterItemFrequency);
        frequencyDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFrequency = adapterView.getItemAtPosition(i).toString();
                Log.e("frequencyDropdown.setOnItemClickListener", "Clicked frequency dropdown");
                setRecyclerView(selectedFrequency, selectedCategory);
            }
        });

        //Dropdown Menu Category
        cateogryDropdown = findViewById(R.id.category_dropdown_autocomplete);
        adapterItemCategory = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, cateogryOptions);
        cateogryDropdown.setAdapter(adapterItemCategory);
        cateogryDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("cateogryDropdown.setOnItemClickListener", "Clicked category dropdown");
                selectedCategory = adapterView.getItemAtPosition(i).toString();
                setRecyclerView(selectedFrequency, selectedCategory);
            }
        });frequencyDropdown.setText(selectedFrequency, false);
        cateogryDropdown.setText(selectedCategory, false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionHistory.this,HomePageActivity.class);
                startActivity(intent);
                TransactionHistory.this.finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void setRecyclerView(String selectedFrequency, String selectedCategory){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, getList(selectedFrequency, selectedCategory));
        recyclerView.setAdapter(adapter);
    }

    private List<TransactionModel> getList(String selectedFrequency, String category){
        //Get current user ID
        String currentUser = MainActivity.loginUser;
        int currentUserID = DB.getUserId(currentUser);

        //Get transaction data for current user
        Cursor res = DB.getDataTransactionFilter(currentUserID, selectedFrequency, selectedCategory);

        List<TransactionModel> transaction_List = new ArrayList<>();

        if(res.getCount() <= 0){
            Toast.makeText(TransactionHistory.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return transaction_List;
        }

        int count = 0;
        while(res.moveToNext()){
            /*
            * Transaction ID    = 0
            * userID            = 1
            * transDate         = 2
            * transCategory     = 3
            * transFrequency    = 4
            * transAmount       = 5 */

            transaction_List.add(new TransactionModel(res.getString(0), res.getString(1), res.getString(2),res.getString(3),res.getString(4),res.getString(5)));
            Log.e("transaction_List", "transaction_List : \nTransID: " + res.getString(0) + "\ntransAmount: " + res.getString(5) + "\ntransDate: " + res.getString(2) + "\ntransFreuency: " + res.getString(4));
            count++;
        }

        Log.e("TransactionHistory", "Final Transaction list size  = " + transaction_List.size());
        Log.e("TransactionHistory", "Total Transactions displayed  = " + count);
        return transaction_List;
    }
}