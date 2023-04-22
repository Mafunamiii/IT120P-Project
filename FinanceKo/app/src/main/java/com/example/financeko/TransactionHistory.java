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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private Button backBtn, ViewHistoryBtn;
    DBHelper DB;
    RecyclerView recyclerView;
    TransactionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        backBtn = findViewById(R.id.backBtn);
        //ViewHistoryBtn = findViewById(R.id.ViewHistoryBtn);
        DB = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        setRecyclerView();


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

    private void setRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this,getList());
        recyclerView.setAdapter(adapter);
    }

    private List<TransactionModel> getList(){
        //Get current user ID
        String currentUser = MainActivity.loginUser;
        int currentUserID = DB.getUserId(currentUser);
        //Get transaction data for current user
        Cursor res = DB.getDataTransaction(currentUserID);

        List<TransactionModel> transaction_List = new ArrayList<>();

        if(res.getCount() <= 0){
            Toast.makeText(TransactionHistory.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return transaction_List;
        }

        StringBuffer buffer = new StringBuffer();

        int count = 0;
        while(res.moveToNext()){
            count ++;
            /*
            * Transaction ID    = 0
            * userID            = 1
            * transDate         = 2
            * transCategory     = 3
            * transFrequency    = 4
            * transAmount       = 5 */


            transaction_List.add(new TransactionModel(res.getString(0), res.getString(1), res.getString(2),res.getString(3),res.getString(4),res.getString(5)));
            Log.e("transaction_List", "transaction_List : \nTransID: " + res.getString(0) + "\ntransAmount: " + res.getString(5) + "\ntransDate: " + res.getString(2));
            Log.e("TransactionHistory", "loop = " + count);
        }

        Log.e("TransactionHistory", "Final Transaction list size  = " + transaction_List.size());
        return transaction_List;
    }
}