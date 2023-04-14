package com.example.financeko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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

}
