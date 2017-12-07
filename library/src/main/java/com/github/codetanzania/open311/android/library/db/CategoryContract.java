package com.github.codetanzania.open311.android.library.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.github.codetanzania.open311.android.library.api.models.ApiService;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceGroup;
import com.github.codetanzania.open311.android.library.models.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.codetanzania.open311.android.library.db.CategoryContract.Entry.TABLE_NAME;


/**
 * This defines a table for categories (called Services in the server).
 */

class CategoryContract {
    static final String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +"("+
            Entry.COLUMN_ID +" TEXT PRIMARY KEY, "+
            Entry.COLUMN_NAME +" TEXT, "+
            Entry.COLUMN_CODE +" TEXT, "+
            Entry.COLUMN_COLOR +" TEXT, "+
            Entry.COLUMN_DESCRIPTION +" TEXT, "+
            Entry.COLUMN_PRIORITY + " INT)";

    static final String DELETE_CATEGORY_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    private static final String CLEAR_CATEGORY_TABLE = "DELETE FROM " + TABLE_NAME;

    private CategoryContract() {}

    static void writeCategories(DatabaseHelper helper, ApiServiceGroup services) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(CLEAR_CATEGORY_TABLE);

        for (ApiService service : services.getServices()) {
            ContentValues values = new ContentValues();
            values.put(Entry.COLUMN_NAME, service.getName());
            values.put(Entry.COLUMN_DESCRIPTION, service.getDescription());
            values.put(Entry.COLUMN_CODE, service.getCode());
            values.put(Entry.COLUMN_COLOR, service.getColor());
            values.put(Entry.COLUMN_ID, service.getId());
            values.put(Entry.COLUMN_PRIORITY, service.getPriority());

            long newRowId = db.insert(TABLE_NAME, null, values);
            System.out.println("New row inserted: "+ newRowId);
        }
    }

    static List<Category> readCategories(DatabaseHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection =  {
                Entry.COLUMN_NAME,
                Entry.COLUMN_ID,
                Entry.COLUMN_PRIORITY,
                Entry.COLUMN_CODE
        };

        Cursor cursor = db.query(TABLE_NAME, projection,null,null,null,null,null);

        // Currently all we use in the app is the category name & id
        List<Category> categories = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME));
            String id = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_ID));
            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(Entry.COLUMN_PRIORITY));
            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_CODE));
            categories.add(new Category(name,id, priority, code));
        }
        cursor.close();

        Collections.sort(categories);

        return categories;
    }

    static class Entry implements BaseColumns {
        static final String TABLE_NAME = "category";

        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_CODE = "code";
        static final String COLUMN_COLOR = "color";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_PRIORITY = "priority";
    }
}
