package com.example.financeko;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    DBHelper DB;
    List<TransactionModel> transaction_List = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = new DBHelper(context);
        Log.e("AlarmReceiver", "AlarmReceiver has been called");
        // Get the current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Calendar calendar = Calendar.getInstance();
        int presentYear = calendar.get(Calendar.YEAR);
        int presentMonth = calendar.get(Calendar.MONTH) + 1; // Note: Calendar.MONTH is zero-based
        int presentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        List<String> message = new ArrayList<>();

        //Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Financeko Notification Channel";
            String description = "Notification Channel for Financeko App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("financeko_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //Start getting data of all transaction with frequencies
        Cursor allTransactions = DB.getAllDataTransactionWithFrequencies();
        int transactionCount = allTransactions.getCount();
        if (transactionCount <= 0) {
            Toast.makeText(context, "No Entry Exists", Toast.LENGTH_SHORT).show();
        }

        //Assign them to a new transaction model to get their properties easier
        int count = 0;
        while (allTransactions.moveToNext()) {
            /*
             * Transaction ID    = 0
             * userID            = 1
             * transDate         = 2
             * transCategory     = 3
             * transFrequency    = 4
             * transAmount       = 5 */

            transaction_List.add(new TransactionModel(allTransactions.getString(0), allTransactions.getString(1), allTransactions.getString(2), allTransactions.getString(3), allTransactions.getString(4), allTransactions.getString(5)));
            //Log.e("transaction_List", "transaction_List : \nTransID: " + allTransactions.getString(0) + "\ntransAmount: " + allTransactions.getString(5) + "\ntransDate: " + allTransactions.getString(2) + "\ntransFreuency: " + allTransactions.getString(4));
            count++;
        }

        //Loop through each transaction inside list and get their frequency
        if (transaction_List == null || transaction_List.isEmpty()) {
            Log.e("transaction_List", "There are no transactions in list");
        } else {
            //For each transaction inside transaction list
            for (TransactionModel transaction :
                    transaction_List) {
                //Check what kind of frequency is current transaction
                String transFrequency = transaction.getFrequency();//get Frequency

                //Get all other details to reduce clutter on insert and update methods
                String transactionID = transaction.getId();
                String transactionOwner = DB.getUsername(Integer.parseInt(transaction.getUserID())); //transactions UserID is passed into getUsername that checks the database for user that has the ID
                String transactionDate = transaction.getDate();
                String transactionCategory = transaction.getCategory();
                int transactionAmount = Integer.parseInt(transaction.getAmount());

                //Convert transaction's date into ints to compare them to present date
                int transactionDay = 0;
                int transactionMonth = 0;
                int transactionYear = 0;

                //Make sick loop from scratch to convert date into int because i cannot find a shortcut solution in google and electricity suddenly went out big pain
                //Could make this shorter but leave it for readability
                //Format : YYYY-MM-DD ; 4 and 7 are dashes
                for (int x = 0; x < transactionDate.length(); x++) {
                    if (x != 4 && x != 7) {
                        int currentNumber = Integer.parseInt(String.valueOf(transactionDate.charAt(x)));
                        if (x == 0) {
                            transactionYear += 1000 * currentNumber;
                        } else if (x == 1) {
                            transactionYear += 100 * currentNumber;
                        } else if (x == 2) {
                            transactionYear += 10 * currentNumber;
                        } else if (x == 3) {
                            transactionYear += currentNumber;
                        } else if (x == 5) {
                            transactionMonth += 10 * currentNumber;
                        } else if (x == 6) {
                            transactionMonth += currentNumber;
                        } else if (x == 8) {
                            transactionDay += 10 * currentNumber;
                        } else if (x == 9) {
                            transactionDay += currentNumber;
                        }
                    }
                }
                Log.e("AlarmReceiver", "Convert YYYY-MM-DD to ints" +
                        "\n unconverted date: " + transactionDate +
                        "\nConversion values - " +
                        "\ntransactionYear: " + transactionYear +
                        "\ntransactionMonth: " + transactionMonth +
                        "\ntransactionDay: " + transactionDay);

                /*Compare the current transaction's day / month / year to present date
                        if less than present date:
                            -Add new transaction with same details and set "Once" for its frequency

                            if transaction is monthly or yearly
                            -Update current transaction's date to current month or year to prevent adding a new transaction when AlarmReciever is called daily
                 */

                if (transFrequency.equals("Daily") && transactionYear <= presentYear && transactionMonth <= presentMonth) {
                    //If transaction date is greater than present date, do nothing
                    if(transactionYear == presentYear && transactionMonth == presentMonth && transactionDay < presentDayOfMonth){
                        DB.insertDataTransaction(transactionOwner, transactionDate, transactionCategory, AddTransaction.Frequency[0], transactionAmount);
                        //Update the date to present date so it wont be called
                        DB.updateDataTransaction(transactionID, currentDate, transactionCategory, transFrequency, transactionAmount);
                        message.add("Daily");
                        Log.e("Daily Transaction","A daily transaction has been added");
                    }

                    //2012-12-03    2012-12-03
                } else if (transFrequency.equals("Monthly") && transactionYear <= presentYear) {
                    //If same year but transaction month is greater than present month
                    if (transactionYear == presentYear && transactionMonth < presentMonth) {
                        //Add transaction
                        DB.insertDataTransaction(transactionOwner, transactionDate, transactionCategory, AddTransaction.Frequency[0], transactionAmount);

                        //Update the month of reoccurring transaction
                        DB.updateDataTransaction(transactionID, currentDate, transactionCategory, transFrequency, transactionAmount);
                        message.add("Monthly");
                        Log.e("Monthly Transaction","A monthly transaction has been added");
                    }

                } else if (transFrequency.equals("Yearly") && transactionYear < presentYear) {
                    //Add transaction
                    DB.insertDataTransaction(transactionOwner, transactionDate, transactionCategory, AddTransaction.Frequency[0], transactionAmount);
                    //Update the year of reoccurring transaction
                    DB.updateDataTransaction(transactionID, currentDate, transactionCategory, transFrequency, transactionAmount);
                    message.add("Yearly");
                    Log.e("Yearly Transaction","A monthly transaction has been added");
                }

                if(!message.isEmpty()){
                    //Format message
                    String FormattedMessage ="";
                    for(int x = 0; x < message.size(); x++){
                        if(x==0){
                            FormattedMessage = message.get(x);
                        }else if(x>=1){
                            FormattedMessage.concat(", " + message.get(x).toLowerCase());
                        }
                    }
                    FormattedMessage.concat(" transactions have been added");
                    String title = "Transactions Added";
                    buildNotification(context, title, FormattedMessage);
                }
            }
        }
    }

    private void buildNotification(Context context, String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "financeko_channel")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
    }
    public String convertIntIntoDate(int year, int month, int day){
        String date;
        String newMonth, newDay;

        if(month<10)
            newMonth = "0" + month;
        else newMonth = Integer.toString(month);

        if(day<10)
            newDay = "0" + day;
        else newDay = Integer.toString(day);

        return date = year + "-" + newMonth + "-" + newDay;
    }
}
