package com.example.firsttry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatenbankHelper extends SQLiteOpenHelper {
    private static final String DATENBANK_NAME = "artikel_db";
    private static final String ARTIKEL_TABELLE_NAME = "artikel";
    private static final String SPALTE_ARTIKELNUMMER = "artikelnummer";
    private static final String SPALTE_BESCHREIBUNG = "beschreibung";

    private static final int DATENBANK_VERSION = 1;

    private final Context context;

    public DatenbankHelper(Context context) {
        super(context, DATENBANK_NAME, null, DATENBANK_VERSION);
        this.context = context;
        kopiereDatenbank();
    }

    private void kopiereDatenbank() {
        try {
            InputStream inputStream = context.getAssets().open(DATENBANK_NAME);

            String datenbankOrdnerPfad = context.getDatabasePath(DATENBANK_NAME).getParent();
            String datenbankZielPfad = context.getDatabasePath(DATENBANK_NAME).getPath();

            File datenbankOrdner = new File(datenbankOrdnerPfad);
            if (!datenbankOrdner.exists()) {
                datenbankOrdner.mkdirs();
            }

           OutputStream outputStream = new FileOutputStream(datenbankZielPfad);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("DatenbankHelper", "Fehler beim Kopieren der Datenbank", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void addArtikel(Artikel artikel) {}

    public int updateArtikel(Artikel artikel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SPALTE_ARTIKELNUMMER, artikel.getArtikelnummer());
        values.put(SPALTE_BESCHREIBUNG, artikel.getBeschreibung());
        int rowsAffected = db.update(ARTIKEL_TABELLE_NAME, values, SPALTE_ARTIKELNUMMER + " = ?",
                new String[]{String.valueOf(artikel.getArtikelnummer())});
        db.close();
        return rowsAffected;
    }

    public Artikel getArtikel(String artikelnummer) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ARTIKEL_TABELLE_NAME, new String[]{SPALTE_ARTIKELNUMMER,
                        SPALTE_BESCHREIBUNG}, SPALTE_ARTIKELNUMMER + "=?",
                new String[]{artikelnummer}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Artikel artikel = new Artikel(cursor.getString(0), cursor.getString(1));
        cursor.close();
        return artikel;
    }
}