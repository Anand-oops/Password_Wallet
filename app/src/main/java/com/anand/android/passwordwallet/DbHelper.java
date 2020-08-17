package com.anand.android.passwordwallet;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION= 1;
    private static final String DATABASE_NAME = "store";

    private static final String TABLE_USER = "user";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_USER +"(email text primary key, password text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists TABLE_USER");
        onCreate(sqLiteDatabase);
    }

    public boolean insert(String email, String pass) {
        SQLiteDatabase db =this. getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("email",email);
        contentValues.put("password",pass);
        long ins=db.insert(TABLE_USER,null,contentValues);
        db.close();
        return ins != -1;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * from " + TABLE_USER + " where email=?", new String[]{email});
        return cursor.getCount() <= 0;
    }

    public boolean checked(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * from " + TABLE_USER + " where email=? and password=?", new String[]{email, pass});
        return cursor.getCount() > 0;
    }

    public boolean update(String email, String NewPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_USER + " SET password=? WHERE email=?", new String[]{NewPassword, email});
        return true;
    }
}
