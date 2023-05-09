package com.example.firsttry;

import android.os.Parcel;
import android.os.Parcelable;

public class Artikel implements Parcelable {

    private String artikelnummer;
    private String beschreibung;

    public Artikel(String artikelnummer, String beschreibung) {
        this.artikelnummer = artikelnummer;
        this.beschreibung = beschreibung;
    }

    protected Artikel(Parcel in) {
        artikelnummer = in.readString();
        beschreibung = in.readString();
    }

    public static final Creator<Artikel> CREATOR = new Creator<Artikel>() {
        @Override
        public Artikel createFromParcel(Parcel in) {
            return new Artikel(in);
        }

        @Override
        public Artikel[] newArray(int size) {
            return new Artikel[size];
        }
    };

    public String getArtikelnummer() {
        return artikelnummer;
    }

    public void setArtikelnummer(String artikelnummer) {
        this.artikelnummer = artikelnummer;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artikelnummer);
        dest.writeString(beschreibung);
    }
}