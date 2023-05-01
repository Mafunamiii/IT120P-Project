package com.example.financeko;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    String[] Categories = {"Transportation", "Bills", "Necessity", "Food", "Others"};
    String[] Frequency = {"Once", "Daily", "Monthly", "Yearly"};
    Context context;
    List<TransactionModel> transactionList;

    DBHelper DB;

    public TransactionAdapter(Context context, List<TransactionModel> transactionList){
        this.context = context;
        this.transactionList = transactionList;

        DB = new DBHelper(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        if(transactionList != null || transactionList.size() > 0){
            TransactionModel model = transactionList.get(position);
            holder.tableCategory.setText(model.getCategory());
            holder.tableAmount.setText(model.getAmount());
            holder.tableDate.setText(model.getDate());
        }else
            return;
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tableCategory, tableAmount, tableDate;
        Button deleteBtn, updateBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tableCategory = itemView.findViewById(R.id.tableCategory);
            tableAmount = itemView.findViewById(R.id.tableAmount);
            tableDate = itemView.findViewById(R.id.tableDate);
            updateBtn = itemView.findViewById(R.id.updateBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Retrieve the instance at this position
                        TransactionModel model = transactionList.get(position);

                        DB.deleteDataTransaction(model.id);

                        // Remove the instance from the list
                        transactionList.remove(position);

                        // Notify the adapter that an item has been removed
                        notifyItemRemoved(position);
                    }
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Retrieve the instance at this position
                        TransactionModel model = transactionList.get(position);
                        String transactionID = model.getId();

                        // Create an intent to start the EditTransaction activity
                        Intent intent = new Intent(context, EditTransaction.class);

                        if(intent != null){
                            Log.e("Transaction Adapter", "INTENT NOT NULL");
                            Log.e("Transaction Adapter", "Transaction ID: " + transactionID +
                                                                "\nCategory: " + model.getCategory() +
                                                                "\nAmount: " + model.getAmount() +
                                                                "\nDate: " + model.getDate());
                        }

                        // Pass the ID of the transaction to be edited to the EditTransaction activity
                        intent.putExtra("transactionModel", model);

                        // Start the EditTransaction activity
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
