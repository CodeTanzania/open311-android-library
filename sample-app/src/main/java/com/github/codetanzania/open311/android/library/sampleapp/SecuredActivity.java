package com.github.codetanzania.open311.android.library.sampleapp;

import android.os.Bundle;
import android.util.Log;

import com.github.codetanzania.open311.android.library.ui.auth.SecureCompactActivity;

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
