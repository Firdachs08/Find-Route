package com.firda.route;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRESENSI = "riwayat_presensi";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATETIME = "dateTime";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " +
            TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FULL_NAME + " TEXT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_PASSWORD + " TEXT" + ")";

    private static final String CREATE_TABLE_PRESENSI = "CREATE TABLE " +
            TABLE_PRESENSI + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_LOCATION + " TEXT, " +
            COLUMN_DATETIME + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRESENSI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENSI);
        onCreate(db);
    }

    public long addUser(String fullName, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public long addRiwayatPresensi(String username, String location, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_DATETIME, dateTime);
        long id = db.insert(TABLE_PRESENSI, null, values);
        db.close();
        return id;
    }


    public boolean checkUser(String username, String password) {
        String[] columns = {
                COLUMN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public String getFullName(String username) {
        String fullName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_FULL_NAME};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            int fullNameIndex = cursor.getColumnIndex(COLUMN_FULL_NAME);
            if (fullNameIndex != -1) { // Pastikan indeks kolom ditemukan
                fullName = cursor.getString(fullNameIndex);
            }
            cursor.close();
        }
        db.close();
        return fullName;
    }



    // Fungsi untuk mencari username berdasarkan fullName
    public String getUsernameByFullName(String fullName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = null;

        // Lakukan kueri ke database untuk mencari username berdasarkan fullName
        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_FULL_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fullName});

        // Periksa apakah cursor bergerak ke baris pertama dan hasil kueri tidak kosong
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            int columnIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            // Pastikan bahwa indeks kolom ditemukan
            if (columnIndex != -1) {
                username = cursor.getString(columnIndex);
            }
        }

        // Tutup cursor dan kembalikan nilai username
        cursor.close();
        return username;
    }

    // Metode untuk memperbarui nama lengkap pengguna berdasarkan username
    public void updateFullName(String username, String newFullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, newFullName);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }
}
