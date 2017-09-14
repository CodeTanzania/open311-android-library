package com.example.krtonga.simplemajifixapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.majifix311.ui.ProblemListActivity;
import com.example.majifix311.ui.ReportProblemActivity;

public class HomeActivity extends FragmentActivity implements View.OnClickListener {
    private Button mReportButton;
    private Button mListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mReportButton = (Button) findViewById(R.id.btn_report);
        mListButton = (Button) findViewById(R.id.btn_list);
        mReportButton.setOnClickListener(this);
        mListButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report :
                Intent startReportIntent = new Intent(this, ReportProblemActivity.class);
                startActivity(startReportIntent);
                break;
            case R.id.btn_list :
                Intent startListIntent = new Intent(this, ProblemListActivity.class);
                startActivity(startListIntent);
                break;
        }
    }
}
