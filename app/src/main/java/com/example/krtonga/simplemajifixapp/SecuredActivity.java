package com.example.krtonga.simplemajifixapp;

import android.os.Bundle;
import android.util.Log;

import com.example.majifix311.ui.auth.SecureCompactActivity;

public class SecuredActivity extends SecureCompactActivity {

    public static final String TAG = SecuredActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secured);
    }

    @Override
    protected void onParty() {
        Log.d(TAG, this.party.toString());
    }
}
