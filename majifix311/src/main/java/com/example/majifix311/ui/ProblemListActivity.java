package com.example.majifix311.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.majifix311.R;

/**
 * This activity shows a list of reported issues.
 */

public class ProblemListActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    //TODO Move logic for making/responding-to network/db calls into here
}
