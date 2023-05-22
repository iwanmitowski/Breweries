package com.example.breweries.Models;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper  extends SQLiteOpenHelper {
    // Database details
    private static final String DATABASE_NAME = "Breweries.db";
    private static final int DATABASE_VERSION = 1;

    // Table details
    private static final String TABLE_NAME = "FavouriteBreweries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BREWERY_ID = "breweryId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CITY = "country";
    private static final String COLUMN_COUNTRY = "city";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LONG = "long";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BREWERY_ID + " TEXT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_CITY + " TEXT, " +
            COLUMN_COUNTRY + " TEXT, " +
            COLUMN_LAT + " DOUBLE, " +
            COLUMN_LONG + " DOUBLE)";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;

        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public void insert(Brewery brewery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BREWERY_ID, brewery.getId());
        values.put(COLUMN_NAME, brewery.getName());
        values.put(COLUMN_CITY, brewery.getCity());
        values.put(COLUMN_COUNTRY, brewery.getCountry());
        values.put(COLUMN_LAT, brewery.getLatitude());
        values.put(COLUMN_LONG, brewery.getLongitude());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<Brewery> get(String searchParameter) {
        List<Brewery> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        if (searchParameter != null) {
            selection =
                COLUMN_NAME + " LIKE ? OR " +
                COLUMN_CITY + " LIKE ? OR " +
                COLUMN_COUNTRY + " LIKE ?";
            selectionArgs = new String[]{ "%" + searchParameter + "%", "%" + searchParameter + "%", "%" + searchParameter + "%" };
        }

        String orderBy = COLUMN_ID + " DESC";

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_BREWERY_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String city = cursor.getString(cursor.getColumnIndex(COLUMN_CITY));
                String country = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY));
                Float latitude = cursor.getFloat(cursor.getColumnIndex(COLUMN_LAT));
                Float longitude = cursor.getFloat(cursor.getColumnIndex(COLUMN_LONG));

                Brewery model = new Brewery(id, name, city, country, latitude, longitude);
                dataList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return dataList;
    }



    public void update(String breweryId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.update(TABLE_NAME, values, COLUMN_BREWERY_ID + " = ?", new String[]{ String.valueOf(breweryId) });
        db.close();
    }

    public void delete(String breweryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_BREWERY_ID + " = ?", new String[]{ String.valueOf(breweryId) });
        db.close();
    }

    public boolean isBreweryExisting(String breweryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT EXISTS(SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_BREWERY_ID + " = '" + breweryId + "' LIMIT 1);";
        Cursor cursor = db.rawQuery(query, null);

        boolean exists = false;
        if (cursor != null && cursor.moveToFirst()) {
            exists = cursor.getInt(0) == 1;
            cursor.close();
        }

        return exists;
    }
}
