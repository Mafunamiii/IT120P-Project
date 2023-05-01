package com.example.financeko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBName = "Login.db";

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        //User data
        MyDB.execSQL("create Table users(userID INTEGER primary key autoincrement, username TEXT, password TEXT, firstname TEXT, lastname TEXT, email TEXT)");

        //Transactions
        MyDB.execSQL("create Table transactions(id INTEGER primary key autoincrement, userID INTEGER, transDate TEXT, transCategory TEXT, transFrequency TEXT, transAmount INTEGER,foreign key('userID') references users (userID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
    }

    public Boolean insertDataTransaction(String username, String transDate, String transCategory, String transFrequency, int transAmount){
        int userID = getUserId(username);

        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userID",userID);
        contentValues.put("transDate",transDate);
        contentValues.put("transCategory",transCategory);
        contentValues.put("transFrequency",transFrequency);
        contentValues.put("transAmount",transAmount);
        long result = MyDB.insert("transactions", null, contentValues);
        if(result == -1){
            return false;
        }
        else
            return true;
    }
    public Boolean updateDataTransaction(String id, String transDate, String transCategory, String transFrequency, int transAmount){

        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("transDate",transDate);
        contentValues.put("transCategory",transCategory);
        contentValues.put("transFrequency",transFrequency);
        contentValues.put("transAmount",transAmount);
        Cursor cursor = MyDB.rawQuery("Select * from transactions where id = ?", new String[]{id});
        if(cursor.getCount()>0){
            long result = MyDB.update("transactions", contentValues, "id=?", new String[]{id});
            if(result == -1){
                return false;
            }
            else
                return true;
        }else{
            return false;
        }
    }

    public Boolean insertDataUser(String username, String password, String firstname, String lastname, String email){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",username);
        contentValues.put("password",password);
        contentValues.put("firstname",firstname);
        contentValues.put("lastname",lastname);
        contentValues.put("email",email);
        long result = MyDB.insert("users", null, contentValues);
        if(result == -1){
            return false;
        }
        else
            return true;
    }


    public Boolean checkUsername(String username){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ?", new String[]{username});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public Boolean checkEmail(String email){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ?", new String[]{email});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public Boolean validateUser(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and password = ?", new String[] {username,password});
        if (cursor.getCount() > 0){
            return true;
        }
        else
            return false;
    }

    public String getUsername(int userID){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where userID = ?", new String[] {String.valueOf(userID)});

        String username = "None";

        Log.e("getUsername", "Finding username with id: " + userID);
        if(cursor !=null){
            if (cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex("username");
                Log.e("columnIndex", "columnIndex: " + columnIndex);
                if(columnIndex >= 0){
                    username = cursor.getString(columnIndex);
                    Log.e("getUsername", "Found user: " + username + "; with userID: " + userID);
                }else{
                    Log.e("getColumnIndex", "userID column not found");
                }
            }else{
                Log.e("username", "No rows found for username with id: " + userID);
            }
            cursor.close();
        }else{
            Log.e("getUsername", "Cursor is null");
        }
        return username;
    }


    public int getUserId(String username){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select userID from users where username = ?", new String[] {username});

        int userID = -1;
        if(cursor != null){
            if (cursor.moveToFirst() && cursor.getColumnCount()>0){
                int columnIndex = cursor.getColumnIndex("userID");
                if(columnIndex >= 0){
                    userID = cursor.getInt(columnIndex);
                }else{
                    Log.e("getColumnIndex", "userID column not found");
                }
            }else{
                Log.e("getUserID", "No rows found for username: " + username);
            }
            cursor.close();
        }else{
            Log.e("getUserId", "Cursor is null");
        }
        return userID;
    }

    //Returns transactions that is owned by the inputUserID
    public Cursor getDataTransaction(int inputUserID){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select *  from transactions where userID = ?", new String[]{String.valueOf(inputUserID)});
        return cursor;
    }

    //Returns all transaction data
    public Cursor getDataTransactionAll(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select *  from transactions", null);
        return cursor;
    }

    //Returns all transaction that does not have the frequency "Once"
    public Cursor getAllDataTransactionWithFrequencies(){
        Log.e("getAllDataTransactionWithFrequencies", "calling getAllDataTransactionWithFrequencies");
        SQLiteDatabase DB = this.getWritableDatabase();
        String frequency = "%Once";

        Cursor cursor = DB.rawQuery("Select *  from transactions where transFrequency not like ?", new String[]{frequency});
        Log.e("getAllDataTransactionWithFrequencies", "cursor count:" + cursor.getCount());
        return cursor;
    }

    //Return all transaction of same frequency and category
    public Cursor getDataTransactionFilter(int inputUserID, String frequency, String category){
        Log.e("getDataTransactionFilter", "calling getDataTransactionFilter");
        SQLiteDatabase DB = this.getWritableDatabase();

        // % will match any string so it can be any option
        if (frequency == null || frequency.equals("All") || frequency.isEmpty()) {
            frequency = "%";
            Log.e("getDataTransactionFilter", "frequency: " + frequency);
        }

        Log.e("getDataTransactionFilter", "non converted category: " + category);
        if (category == null || category.equals("All") || category.isEmpty()) {
            category = "%";
            Log.e("getDataTransactionFilter", "category: " + category);
        }
        Log.e("getDataTransactionFilter", "\nFinal frequency: " + frequency + "\nFinal category: " + category);

        Cursor cursor = DB.rawQuery("Select *  from transactions where userID = ? AND (transFrequency LIKE ? OR transFrequency IS NULL) AND (transCategory LIKE ? OR transCategory IS NULL)", new String[]{String.valueOf(inputUserID), frequency, category});
        Log.e("getDataTransactionFilter", "cursor count:" + cursor.getCount());
        return cursor;
    }



    public boolean deleteDataTransaction(String id) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.delete("transactions", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getSumTransaction(int inputUserID){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT SUM(amount) FROM transactions WHERE userID = ?", new String[]{String.valueOf(inputUserID)});
        return cursor;
    }

    //Returns sum of transaction each month
    public int getSumMonth(int inputUserID, int month){
        int totalSum = 0;
        if(month >12 || month <1){
            Log.e("getSumMonth", "Month does not exist: " + month);
            return totalSum;
        }
        SQLiteDatabase DB = this.getWritableDatabase();

        // Execute the rawQuery to retrieve the sum of transactions for the given user and month
        Cursor cursor = DB.rawQuery("SELECT SUM(transAmount) FROM transactions WHERE userID=? AND strftime('%m', transDate)=? AND transFrequency = ?",
                new String[]{String.valueOf(inputUserID), String.format("%02d", month), "Once"});
        if(cursor.moveToFirst()){
            totalSum = cursor.getInt(0);
        }
        cursor.close();
        DB.close();
        return totalSum;
    }

    //Returns sum of inputted category
    public int getSumCategory(int inputUserID, String category){
        int totalSum = 0;
        if(!Arrays.asList(AddTransaction.Categories).contains(category)){
            Log.e("DBHelper","Input category does not exist in static AddTransaction.Categories");
            return totalSum;
        }

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT SUM(transAmount) FROM transactions WHERE userID = ? AND transCategory=?", new String[]{String.valueOf(inputUserID), category});
        if(cursor.moveToFirst()){
            totalSum = cursor.getInt(0);
        }
        return totalSum;
    }

    //Returns sum of inputted category with a frequency filter
    public int getSumCategoryWithFrequencyFilter(int inputUserID, String category, String frequency){
        int totalSum = 0;
        if(!Arrays.asList(AddTransaction.Categories).contains(category)){
            Log.e("DBHelper","Input category does not exist in static AddTransaction.Categories");
            return totalSum;
        }

        // % will match any string so it can be any option
        if (frequency == null || frequency.equals("All") || frequency.isEmpty()) {
            frequency = "%";
            Log.e("getDataTransactionFilter", "frequency: " + frequency);
        }

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT SUM(transAmount) FROM transactions WHERE userID = ? AND transCategory=? AND (transFrequency LIKE ? OR transFrequency IS NULL)", new String[]{String.valueOf(inputUserID), category, frequency});
        if(cursor.moveToFirst()){
            totalSum = cursor.getInt(0);
            Log.e("getSumCategoryWithFrequencyFilter", "Count: " + cursor.getCount());
        }
        return totalSum;
    }

    //Returns sum of inputted frequency
    public int getSumFrequency(int inputUserID, String frequency){
        int totalSum = 0;
        if(!Arrays.asList(AddTransaction.Frequency).contains(frequency)){
            Log.e("DBHelper","Input frequency does not exist in static AddTransaction.Frequency");
            return totalSum;
        }

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT SUM(transAmount) FROM transactions WHERE userID = ? AND transFrequency=?", new String[]{String.valueOf(inputUserID), frequency});
        if(cursor.moveToFirst()){
            totalSum = cursor.getInt(0);
            Log.e("getSumFrequency", "Total count: "+ cursor.getCount());
        }
        return totalSum;
    }
}
