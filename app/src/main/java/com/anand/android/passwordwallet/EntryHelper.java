package com.anand.android.passwordwallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class EntryHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SyncToDrive.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ENTRIES = "entries";
    private static String ENTRIES_ID = "id";
    private static final String ENTRIES_NAME = "name";
    private static final String ENTRIES_USER = "username";
    private static final String ENTRIES_PASSWORD = "password";
    private static final String ENTRIES_NOTE = "note";

    public EntryHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_WEBSITES = "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRIES + "(" + ENTRIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ENTRIES_NAME + " TEXT , " + ENTRIES_USER + " TEXT, " + ENTRIES_PASSWORD + " TEXT, " + ENTRIES_NOTE + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE_WEBSITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists TABLE_ENTRIES");
        onCreate(sqLiteDatabase);
    }

    public boolean insert(String name, String userId, String password, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRIES_NAME, name);
        contentValues.put(ENTRIES_USER, userId);
        contentValues.put(ENTRIES_PASSWORD, password);
        contentValues.put(ENTRIES_NOTE, note);
        long ins = db.insert(TABLE_ENTRIES, null, contentValues);
        db.close();
        return ins != -1;
    }

    public Cursor ViewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + TABLE_ENTRIES;
        return db.rawQuery(query, null);
    }

    public EntryClass getRow(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ENTRIES + " WHERE " + ENTRIES_ID + " = " + id + "", null);
        cursor.moveToFirst();
        EntryClass entry = new EntryClass(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4));
        cursor.close();
        db.close();
        return entry;
    }

    public boolean updateRow(int id, String name, String user, String pass, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRIES_NAME, name);
        contentValues.put(ENTRIES_USER, user);
        contentValues.put(ENTRIES_PASSWORD, pass);
        contentValues.put(ENTRIES_NOTE, note);
        db.update(TABLE_ENTRIES, contentValues, ENTRIES_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return true;
    }

    public void deleteRow(EntryClass entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES, ENTRIES_ID + " =?", new String[]{String.valueOf(entry.getId())});
        db.close();
    }

    public void deleteDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_ENTRIES);
        Log.i(TAG, "deleteDatabase: DELETED");
        onCreate(db);
        db.close();
    }
}
