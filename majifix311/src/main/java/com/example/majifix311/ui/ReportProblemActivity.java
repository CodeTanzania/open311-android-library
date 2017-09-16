package com.example.majifix311.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.majifix311.EventHandler;
import com.example.majifix311.Problem;
import com.example.majifix311.R;
import com.example.majifix311.api.ReportService;
import com.example.majifix311.utils.EmptyErrorTrigger;

/**
 * This activity is for submitting problems to a municipal company that uses the majifix system.
 */

public class ReportProblemActivity extends FragmentActivity implements View.OnClickListener, Problem.Builder.InvalidCallbacks {
    private Problem.Builder mBuilder;
    private boolean mIsLocation;

    private TextInputLayout mTilName;
    private TextInputLayout mTilNumber;
    private TextInputLayout mTilCategory;
    private TextInputLayout mTilAddress;
    private TextInputLayout mTilDescription;
    private EditText mEtName;

    private EditText mEtPhone;
    private EditText mEtCategory;
    private EditText mEtAddress;
    private EditText mEtDescription;

    private LinearLayout mLlLocation;
    private ImageView mIvLocation;
    private TextView mTvLocationError;
    private LinearLayout mLlPhoto;

    private Button mSubmitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_form);

        // find all views
        mTilName = (TextInputLayout) findViewById(R.id.til_name);
        mTilNumber = (TextInputLayout) findViewById(R.id.til_phone);
        mTilCategory = (TextInputLayout) findViewById(R.id.til_category);
        mTilAddress = (TextInputLayout) findViewById(R.id.til_address);
        mTilDescription = (TextInputLayout) findViewById(R.id.til_description);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtCategory = (EditText) findViewById(R.id.et_category);
        mEtAddress = (EditText) findViewById(R.id.et_address);
        mEtDescription = (EditText) findViewById(R.id.et_description);
        mLlLocation = (LinearLayout) findViewById(R.id.ll_add_location);
        mIvLocation = (ImageView) findViewById(R.id.iv_location);
        mTvLocationError = (TextView) findViewById(R.id.tv_location_error);
        mLlPhoto = (LinearLayout) findViewById(R.id.ll_add_photo);

        // for required fields: watch for text changes, and if empty, display error
        mEtName.addTextChangedListener(new EmptyErrorTrigger(mTilName));
        mEtPhone.addTextChangedListener(new EmptyErrorTrigger(mTilNumber));
        mEtCategory.addTextChangedListener(new EmptyErrorTrigger(mTilCategory));
        mEtAddress.addTextChangedListener(new EmptyErrorTrigger(mTilAddress));
        mEtDescription.addTextChangedListener(new EmptyErrorTrigger(mTilDescription));

        // add click listener to submit button
        mSubmitButton = (Button) findViewById(R.id.btn_submit);
        mSubmitButton.setOnClickListener(this);

        // initialize problem builder
        mBuilder = new Problem.Builder(this);
        registerForPostUpdates();
    }

    private void registerForPostUpdates() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO replace with real logic
                Toast.makeText(getApplicationContext(), "Hello Receiver!", Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(EventHandler.BROADCAST_REPORT_RECIEVED));
    }

    private void submit() {
        // Creates a problem using a builder which will validate required inputs
        mBuilder.setUsername(mEtName.getText().toString());
        mBuilder.setPhoneNumber(mEtPhone.getText().toString());
        mBuilder.setCategory(mEtCategory.getText().toString());
        mBuilder.setAddress(mEtAddress.getText().toString());
        mBuilder.setDescription(mEtDescription.getText().toString());
        Problem problem = mBuilder.build();

        if (problem != null) {
            ReportService.postNewProblem(ReportProblemActivity.this, problem);
        }
    }

    public void setGpsPoint(Location location) {
        if (location == null) {
            updateLocationIcon(true);
        }
        mBuilder.setLocation(location);
        updateLocationIcon(false);
    }

    public void updateLocationIcon(boolean isError) {
        if (isError) {
            mIvLocation.setImageResource(R.drawable.ic_add_location_error);
            mTvLocationError.setVisibility(View.VISIBLE);
        } else {
            mIvLocation.setImageResource(R.drawable.ic_add_location_black);
            mTvLocationError.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onInvalidUsername() {
        mTilName.setError(getString(R.string.required));
    }

    @Override
    public void onInvalidPhoneNumber() {
        mTilNumber.setError(getString(R.string.required));
    }

    @Override
    public void onInvalidCategory() {
        mTilCategory.setError(getString(R.string.required));
    }

    @Override
    public void onInvalidLocation() {
        updateLocationIcon(true);
    }

    @Override
    public void onInvalidAddress() {
        mTilAddress.setError(getString(R.string.required));
    }

    @Override
    public void onInvalidDescription() {
        mTilDescription.setError(getString(R.string.required));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            submit();
        }
    }
}
