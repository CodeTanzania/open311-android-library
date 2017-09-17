package com.example.majifix311.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.majifix311.api.models.ApiServiceGroup;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * This is used to manage database tables.
 *
 * Thanks to: http://beust.com/weblog/2015/06/01/easy-sqlite-on-android-with-rxjava/
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MajiFix.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoryContract.CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // as this is a cache for online data, so throw out existing data and start over
        db.execSQL(CategoryContract.DELETE_CATEGORY_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // as this is a cache for online data, so throw out existing data and start over
        onUpgrade(db, oldVersion, newVersion);
    }

    public void setCategories(final ApiServiceGroup categories, Consumer<List<String>> onNext, Consumer<Throwable> onError, boolean async) {
        Observable<List<String>> categoriesTask = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                CategoryContract.writeCategories(DatabaseHelper.this, categories);
                List<String> proof = CategoryContract.readCategories(DatabaseHelper.this);
                e.onNext(proof);
                e.onComplete();
            }
        });
        if (async) {
            categoriesTask.subscribe(onNext, onError);
        } else {
            categoriesTask.blockingSubscribe(onNext, onError);
        }
    }

    public void getCategories(Consumer<List<String>> onNext, Consumer<Throwable> onError, boolean async) {
        Observable<List<String>> categoriesTask = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                List<String> result = CategoryContract.readCategories(DatabaseHelper.this);
                e.onNext(result);
                e.onComplete();
            }
        });

        if (async) {
            categoriesTask.subscribe(onNext, onError);
        } else {
            categoriesTask.blockingSubscribe(onNext, onError);
        }
    }

}
