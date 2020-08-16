package com.anand.android.passwordwallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION= 1;
    private static final String DATABASE_NAME = "store";

    private static final String TABLE_USER = "user";
    private static final String TABLE_WEBSITES = "websites";

    private static final String WEBSITES_FIELD_ID = "id";
    private static final String WEBSITES_FIELD_URL = "url";
    private static final String WEBSITES_FIELD_USER = "username";
    private static final String WEBSITES_FIELD_PASSWORD = "password";

    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_USER +"(email text primary key, password text)");
        //String CREATE_TABLE_WEBSITES = "CREATE TABLE " + TABLE_WEBSITES + "("+ WEBSITES_FIELD_ID +" INTEGER PRIMARY KEY, "+
                //WEBSITES_FIELD_URL + " TEXT, "+ WEBSITES_FIELD_USER + " TEXT, "+ WEBSITES_FIELD_PASSWORD + " TEXT);";
        //sqLiteDatabase.execSQL(CREATE_TABLE_WEBSITES);
        //L.Log("Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists TABLE_USER");
    }

    public boolean insert(String email, String pass) {
        SQLiteDatabase db =this. getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("email",email);
        contentValues.put("password",pass);
        long ins=db.insert(TABLE_USER,null,contentValues);
        db.close();
        if (ins==-1) return false;
        else return true;
    }

    public Boolean chkemail(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * from "+TABLE_USER+" where email=?" ,new String[]{email});
        if (cursor.getCount()>0) return false;
        else return true;
    }

    public Boolean checked(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * from "+TABLE_USER+" where email=? and password=?" ,new String[]{email,pass});
        if (cursor.getCount()>0) return true;
        else return false;
    }
}
