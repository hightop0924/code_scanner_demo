package com.example.firsttry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "artikel_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "auswahl";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (!doesDatabaseExist(context)) {
            this.getReadableDatabase(); // Erstellt eine leere Datenbank, die überschrieben wird.
            copyDatabaseFromAssets(context);
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Implementiere hier die Logik zum Erstellen der Datenbank (falls erforderlich).
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implementiere hier die Logik zum Aktualisieren der Datenbank (falls erforderlich).
    }

    private void copyDatabaseFromAssets(Context context) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = context.getAssets().open(DATABASE_NAME);
            String outputPath = context.getDatabasePath(DATABASE_NAME).getPath();
            outputStream = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean doesDatabaseExist(Context context) {
        File databaseFile = context.getDatabasePath(DATABASE_NAME);
        return databaseFile.exists();
    }

    public List<String> getUniqueValuesForColumn(String columnName) {
        SQLiteDatabase db = getReadableDatabase();
        List<String> uniqueValues = new ArrayList<>();
        String query = "SELECT DISTINCT " + columnName + " FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                uniqueValues.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return uniqueValues;
    }

    public List<String> getFilteredValuesForColumns(String columnName, String[] filterColumnNames, String[] filterValues) {
        SQLiteDatabase db = getReadableDatabase();
        List<String> filteredValues = new ArrayList<>();

        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < filterColumnNames.length; i++) {
            whereClause.append(filterColumnNames[i]).append(" = ?");
            if (i < filterColumnNames.length - 1) {
                whereClause.append(" AND ");
            }
        }

        String query = "SELECT DISTINCT " + columnName + " FROM " + TABLE_NAME + " WHERE " + whereClause;
        Cursor cursor = db.rawQuery(query, filterValues);

        if (cursor.moveToFirst()) {
            do {
                filteredValues.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredValues;
    }

    public String getArtikelnummer(String typ, String position, String kiefer, String farbe, String form) {
        SQLiteDatabase db = getReadableDatabase();
        String artikelnummer = null;
        String query = "SELECT artikelnummer FROM " + TABLE_NAME + " WHERE typ = ? AND position = ? AND kiefer = ? AND farbe = ? AND form = ?";

        Cursor cursor = db.rawQuery(query, new String[]{typ, position, kiefer, farbe, form});
        if (cursor.moveToFirst()) {
            artikelnummer = cursor.getString(0);
        }
        cursor.close();
        return artikelnummer;
    }
}