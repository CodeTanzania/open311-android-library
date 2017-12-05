package com.github.codetanzania.open311.android.library.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.github.codetanzania.open311.android.library.MajiFix;
import com.github.codetanzania.open311.android.library.ui.ProblemListActivity;
import com.github.codetanzania.open311.android.library.ui.ReportProblemActivity;
import com.github.codetanzania.open311.android.library.ui.auth.SigninActivity;

public class HomeActivity extends FragmentActivity implements View.OnClickListener {
    private Button mReportButton;
    private Button mSignInButton;
    private Button mSecuredActivityButton;
    private Button mListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mReportButton = (Button) findViewById(R.id.btn_report);
        mSignInButton = (Button) findViewById(R.id.btn_signin);
        mSecuredActivityButton = (Button) findViewById(R.id.btn_secured_activity);
        mListButton = (Button) findViewById(R.id.btn_list);
        mReportButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
        mSecuredActivityButton.setOnClickListener(this);
        mListButton.setOnClickListener(this);

        MajiFix.setup(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report:
                Intent startReportIntent = new Intent(this, ReportProblemActivity.class);
                startActivity(startReportIntent);
                break;
            case R.id.btn_signin:
                Intent startSignInIntent = new Intent(this, SigninActivity.class);
                startActivity(startSignInIntent);
                break;
            case R.id.btn_secured_activity:
                Intent startSecuredActivityIntent = new Intent(this, SecuredActivity.class);
                startActivity(startSecuredActivityIntent);
                break;
            case R.id.btn_list:
                Intent startListIntent = new Intent(this, ProblemListActivity.class);
                startActivity(startListIntent);
                break;
        }
    }
}
