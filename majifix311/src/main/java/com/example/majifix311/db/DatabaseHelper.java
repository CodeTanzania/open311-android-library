package com.example.majifix311.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;

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
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MajiFix.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoryContract.CREATE_CATEGORY_TABLE);
        db.execSQL(ProblemContract.CREATE_PROBLEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // as this is a cache for online data, so throw out existing data and start over
        db.execSQL(CategoryContract.DELETE_CATEGORY_TABLE);
        db.execSQL(ProblemContract.DELETE_PROBLEM_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // as this is a cache for online data, so throw out existing data and start over
        onUpgrade(db, oldVersion, newVersion);
    }

    public void setCategories(final ApiServiceGroup categories, Consumer<List<Category>> onNext, Consumer<Throwable> onError, boolean async) {
        Observable<List<Category>> categoriesTask = Observable.create(new ObservableOnSubscribe<List<Category>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Category>> e) throws Exception {
                CategoryContract.writeCategories(DatabaseHelper.this, categories);
                List<Category> proof = CategoryContract.readCategories(DatabaseHelper.this);
                e.onNext(proof);
                e.onComplete();
            }
        });

        triggerTask(categoriesTask, onNext, onError, async);
    }

    public void getCategories(Consumer<List<Category>> onNext, Consumer<Throwable> onError, boolean async) {
        Observable<List<Category>> categoriesTask = Observable.create(new ObservableOnSubscribe<List<Category>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Category>> e) throws Exception {
                List<Category> result = CategoryContract.readCategories(DatabaseHelper.this);
                e.onNext(result);
                e.onComplete();
            }
        });

        triggerTask(categoriesTask, onNext, onError, async);
    }

    public void saveMyReportedProblems(final List<ApiServiceRequestGet> serverResponse, Consumer<List<Problem>> onNext, Consumer<Throwable> onError, boolean async) {
        Observable<List<Problem>> problemTask = Observable.create(new ObservableOnSubscribe<List<Problem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Problem>> e) throws Exception {
                ProblemContract.writeProblems(DatabaseHelper.this, serverResponse);
                List<Problem> proof = ProblemContract.readProblems(DatabaseHelper.this);
                e.onNext(proof);
                e.onComplete();
            }
        });

        triggerTask(problemTask, onNext, onError, async);
    }

    private void triggerTask(Observable<?> task, Consumer onNext, Consumer<Throwable> onError, boolean async) {
        if (async) {
            task.subscribe(onNext, onError);
        } else {
            task.blockingSubscribe(onNext, onError);
        }
    }

}
