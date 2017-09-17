package com.example.majifix311.api;

import android.content.Context;

import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.db.DatabaseHelper;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * This makes a call to get category types from the server, and sends a broadcast when received.
 */

public class CategoriesManager {
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    public CategoriesManager(Context context) {
        mContext = context;
    }

    public void getCategories() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(mContext);
        }
        MajiFixAPI.getInstance().getServices(onRetrievedFromNetwork(), onError());
    }

    private Consumer<ApiServiceGroup> onRetrievedFromNetwork() {
        return new Consumer<ApiServiceGroup>() {
            @Override
            public void accept(ApiServiceGroup categories) throws Exception {
                System.out.println("onRetrievedFromNetwork! "+categories);
                //EventHandler.sendResultReceived(mContext);
                if (categories != null
                        && categories.getServices() != null
                        && !categories.getServices().isEmpty()) {
                    mDatabaseHelper.setCategories(categories, onSavedInDatabase(), onError(), true);
                }
            }
        };
    }

    private Consumer<List<String>> onSavedInDatabase() {
        return new Consumer<List<String>>() {
            @Override
            public void accept(List<String> categories) throws Exception {
                System.out.println("onRetrievedFromNetwork after Database save! "+categories);
                //EventHandler.sendResultReceived(mContext);
            }
        };
    }

    private Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable error) throws Exception {
                System.out.println("onError! "+error);
                //EventHandler.sendResultReceived(mContext);
            }
        };
    }
}
