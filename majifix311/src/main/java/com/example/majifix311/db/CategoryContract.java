package com.example.majifix311.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.api.models.ApiServiceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * This defines a table for categories (called Services in the server).
 */

class CategoryContract {
    static final String CREATE_CATEGORY_TABLE = "CREATE TABLE "+ Entry.TABLE_NAME +"("+
            Entry.COLUMN_ID +" TEXT PRIMARY KEY, "+
            Entry.COLUMN_NAME +" TEXT, "+
            Entry.COLUMN_CODE +" TEXT, "+
            Entry.COLUMN_COLOR +" TEXT, "+
            Entry.COLUMN_DESCRIPTION +" TEXT)";

    static final String DELETE_CATEGORY_TABLE = "DROP TABLE IF EXISTS "+ Entry.TABLE_NAME;

    private CategoryContract() {}

    static void writeCategories(DatabaseHelper helper, ApiServiceGroup services) {
        SQLiteDatabase db = helper.getWritableDatabase();

        for (ApiService service : services.getServices()) {
            ContentValues values = new ContentValues();
            values.put(Entry.COLUMN_NAME, service.getName());
            values.put(Entry.COLUMN_DESCRIPTION, service.getDescription());
            values.put(Entry.COLUMN_CODE, service.getCode());
            values.put(Entry.COLUMN_COLOR, service.getColor());
            values.put(Entry.COLUMN_ID, service.getId());

            long newRowId = db.insert(Entry.TABLE_NAME, null, values);
        }
    }

    static List<String> readCategories(DatabaseHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection =  {
                Entry.COLUMN_NAME,
                Entry.COLUMN_ID
        };

        Cursor cursor = db.query(Entry.TABLE_NAME, projection,null,null,null,null,null);

        // Currently all we use in the app is the category name
        List<String> categoryNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME));
            categoryNames.add(name);
        }
        cursor.close();

        return categoryNames;
    }

    private static class Entry implements BaseColumns {
        static final String TABLE_NAME = "category";

        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_CODE = "code";
        static final String COLUMN_COLOR = "color";
        static final String COLUMN_DESCRIPTION = "description";
    }
}
