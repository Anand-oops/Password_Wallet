package com.anand.android.passwordwallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntryHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SyncToDrive.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_ENTRIES = "entries";
    private static final String ENTRIES_ID = "id";
    private static final String ENTRIES_NAME = "name";
    private static final String ENTRIES_USER = "username";
    private static final String ENTRIES_PASSWORD = "password";
    private static final String ENTRIES_NOTE = "note";
    private static final String TAG = "EntryHelper";
    private final Context context;

    public EntryHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_WEBSITES = "CREATE TABLE " + TABLE_ENTRIES + "(" + ENTRIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ENTRIES_NAME + " TEXT , " + ENTRIES_USER + " TEXT, " + ENTRIES_PASSWORD + " TEXT, " + ENTRIES_NOTE + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE_WEBSITES);
        Log.i(TAG, "onCreate: TABLE CREATED");
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
        Log.d(TAG, "insert: Value inserted");
        return ins != -1;
    }

    public Cursor ViewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + TABLE_ENTRIES;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            //cursor.moveToFirst();
            return cursor;
        } else
            return null;
    }
}
