package ru.crmkurgan.main.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import ru.crmkurgan.main.Models.PropertyModels;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "realEstate.db";
    public static final String TABLE_FAVOURITE_NAME = "favourite";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATE = "rate";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TABL = "tabl";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_KOMNATNOST = "komnatnost";
    public static final String KEY_PL_OB = "pl_ob";
    public static final String KEY_PLANIR = "planir";
    public static final String KEY_REMONT = "remont";
    public static final String KEY_SANUZEL = "sanuzel";
    public static final String KEY_BALKON = "balkon";
    public static final String KEY_ETASH = "etash";
    public static final String KEY_POSTROY = "postroy";
    public static final String KEY_STENA = "stena";
    public static final String KEY_RAZD_KOMNAT = "razd_komnat";
    public static final String KEY_ELECTRO = "electro";
    public static final String KEY_OTOP = "otop";
    public static final String KEY_GAZ = "gaz";
    public static final String KEY_CANALYA = "canalya";
    public static final String KEY_ZEMLYA = "zemlya";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_RATE + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_TABL + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_KOMNATNOST + " TEXT,"
                + KEY_PL_OB + " TEXT,"
                + KEY_PLANIR + " TEXT,"
                + KEY_REMONT + " TEXT,"
                + KEY_SANUZEL + " TEXT,"
                + KEY_BALKON + " TEXT,"
                + KEY_ETASH + " TEXT,"
                + KEY_POSTROY + " TEXT,"
                + KEY_STENA + " TEXT,"
                + KEY_RAZD_KOMNAT + " TEXT,"
                + KEY_ELECTRO + " TEXT,"
                + KEY_OTOP + " TEXT,"
                + KEY_GAZ + " TEXT,"
                + KEY_CANALYA + " TEXT,"
                + KEY_ZEMLYA + " TEXT"
                + ")";

        db.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        onCreate(db);
    }

    public boolean getFavouriteById(String story_id, String tabl) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,tabl FROM favourite WHERE " + KEY_ID + " =" + story_id + " AND " + KEY_TABL + " = " +tabl, null);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String _id, String _tabl) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM favourite " + " WHERE " + KEY_ID + " = " + _id + " AND " + KEY_TABL + " = " + _tabl);
        db.close();
    }

    public void addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TableName, s1, contentvalues);
    }

    public ArrayList<PropertyModels> getFavourite() {
        ArrayList<PropertyModels> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PropertyModels contact = new PropertyModels();
                contact.setPropid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                contact.setRateAvg(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RATE)));
                contact.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                contact.setTabl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TABL)));
                contact.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)));
                if(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TABL)).equals("1")) {
                    contact.setKomnatnost(cursor.getString(cursor.getColumnIndexOrThrow(KEY_KOMNATNOST)));
                    contact.setPl_ob(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PL_OB)));
                    contact.setPlanir(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLANIR)));
                    contact.setRemont(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REMONT)));
                    contact.setSanuzel(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SANUZEL)));
                    contact.setBalkon(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BALKON)));
                    contact.setEtash(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ETASH)));
                    contact.setPostroy(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSTROY)));
                    contact.setStena(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STENA)));
                }
                if(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TABL)).equals("2")) {
                    contact.setPl_ob(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PL_OB)));
                    contact.setEtash(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ETASH)));
                    contact.setPostroy(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSTROY)));
                    contact.setStena(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STENA)));
                    contact.setRazd_komnat(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RAZD_KOMNAT)));
                    contact.setElectro(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ELECTRO)));
                    contact.setOtop(cursor.getString(cursor.getColumnIndexOrThrow(KEY_OTOP)));
                    contact.setGaz(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GAZ)));
                    contact.setCanalya(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CANALYA)));
                    contact.setZemlya(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ZEMLYA)));
                }

                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
