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
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.majifix311.EventHandler;
import com.example.majifix311.models.Problem;
import com.example.majifix311.R;
import com.example.majifix311.api.ReportService;
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.utils.EmptyErrorTrigger;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * This activity is for submitting problems to a municipal company that uses the majifix system.
 */

public class ReportProblemActivity extends FragmentActivity implements View.OnClickListener,
        Problem.Builder.InvalidCallbacks, CategoryPickerDialog.OnItemSelected {
    private Problem.Builder mBuilder;
    private boolean mIsLocation;

    private String[] mCategories;
    private CategoryPickerDialog mCategoryDialog;

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
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.getCategories(onCategoriesRetrievedFromDb(), onError(), false);

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

        // add click listeners
        mSubmitButton = (Button) findViewById(R.id.btn_submit);
        mSubmitButton.setOnClickListener(this);
        setupCategoryPicker();

        // initialize problem builder
        mBuilder = new Problem.Builder(this);
        registerForPostUpdates();
    }

    private void setupCategoryPicker() {
        // ensures keyboard stays closed on click
        mEtCategory.setInputType(InputType.TYPE_NULL);

        // ensures that dialog opens correctly
        mEtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategoryPickerDialog(mCategories);
            }
        });
        mEtCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // close keyboard if open (for example, when pressing next)
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEtCategory.getWindowToken(), 0);

                    // start dialog
                    createCategoryPickerDialog(mCategories);
                }
            }
        });
    }

    private void createCategoryPickerDialog(String[] categories) {
        if (mCategoryDialog == null) {
            System.out.println("...Category Dialog was null...");
            // creates new dialog
            mCategoryDialog = CategoryPickerDialog.newInstance(categories);

            // registers activity to receive input from dialog
            mCategoryDialog.setListener(this);
        }
        // shows dialog
        mCategoryDialog.show(getFragmentManager(), "dialog");
    }

    private void registerForPostUpdates() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO replace with real logic
                if (intent.getBooleanExtra(EventHandler.IS_SUCCESS, false)) {
                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failure!", Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter(EventHandler.BROADCAST_REPORT_RECIEVED));
    }

    private void submit() {
        // Creates a problem using a builder which will validate required inputs
        mBuilder.setUsername(mEtName.getText().toString());
        mBuilder.setPhoneNumber(mEtPhone.getText().toString());
//        mBuilder.setCategory(mEtCategory.getText().toString());
        //TODO: Don't hardcode category
        mBuilder.setCategory("5968b64148dfc224bb47748d");
        mBuilder.setAddress(mEtAddress.getText().toString());
        //TODO: Don't hardcode location
//        mBuilder.setLocation(new Location(""));
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

    protected Consumer<List<String>> onCategoriesRetrievedFromDb() {
        return new Consumer<List<String>>() {
            @Override
            public void accept(List<String> strings) throws Exception {
                if (strings.size() > 0) {
                    mCategories = strings.toArray(new String[strings.size()]);
                } else {
                    onError().accept(null);
                }
            }
        };
    }

    protected Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable error) throws Exception {
                System.out.println("onError! "+error);
                Toast.makeText(ReportProblemActivity.this,
                        "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                ReportProblemActivity.this.finish();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            submit();
        }
    }

    @Override
    public void onItemSelected(String item, int position) {
        mEtCategory.setText(item);
        mEtCategory.setText(item);
    }
}
