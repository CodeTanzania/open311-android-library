package com.example.majifix311.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
import com.example.majifix311.location.FetchAddressIntentService;
import com.example.majifix311.location.LocationTracker;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.R;
import com.example.majifix311.api.ReportService;
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.ui.views.AttachmentButton;
import com.example.majifix311.utils.AttachmentUtils;
import com.example.majifix311.utils.EmptyErrorTrigger;
import com.example.majifix311.utils.MapUtils;
import com.example.majifix311.utils.KeyboardUtils;
import com.example.majifix311.utils.MapUtils;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * This activity is for submitting problems to a municipal company that uses the majifix system.
 */

public class ReportProblemActivity extends AppCompatActivity implements View.OnClickListener,
        Problem.Builder.InvalidCallbacks, CategoryPickerDialog.OnItemSelected, SelectLocationFragment.OnSelectLocation {
    private static final String SELECT_LOCATION_FRAGMENT_TAG = "select-location-fragment";
    private static final String SELECT_CATEGORY_FRAGMENT_TAG = "category_dialog";

    private Problem.Builder mBuilder;

    private Category[] mCategories;
    private CategoryPickerDialog mCategoryDialog;
    private Category mSelectedCategory;

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
    private LocationTracker mLocationTracker;

    private AttachmentButton mAbPhoto;

    private LinearLayout mLlPhoto;
    private ImageView mIvPhoto;
    private String mAttachmentUrl;

    private Button mSubmitButton;

    private BroadcastReceiver mPostResponse = new BroadcastReceiver() {
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
    };

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
        mAbPhoto = (AttachmentButton) findViewById(R.id.ab_add_photo);

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

        // start location tracker to get current GPS location
        mLocationTracker = new LocationTracker(this);
        mLocationTracker.start(mAutoLocationListener);

        // initialize problem builder
        mBuilder = new Problem.Builder(this);
        registerForPostUpdates();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // This ensures that GPS is turned on correctly
        if (mLocationTracker != null) {
            mLocationTracker.respondToActivityResult(requestCode, resultCode);
        }

        // Attachment button will handle result of camera or file intents, and display image
        boolean photoCapturedSuccess = mAbPhoto.displayOnActivityResult(requestCode, resultCode, data);

        // If sucess, add attachment to problem.
       if (photoCapturedSuccess) {
            Attachment attachment = AttachmentUtils.getPicAsAttachment(mAbPhoto.getAttachmentUrl());
            mBuilder.addAttachment(attachment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // This ensures that permission callbacks are handled correctly
        if (mLocationTracker != null) {
            mLocationTracker.respondToPermissions(requestCode, grantResults);
        }
        // Attachment button will handle photo permissions requests
        mAbPhoto.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        // both broadcasts and locationTrackers can cause memory leaks if not properly destroyed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPostResponse);
        if (mLocationTracker != null) {
            mLocationTracker.onPause();
        }
        super.onDestroy();
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
                    KeyboardUtils.hideSoftInputMethod(ReportProblemActivity.this);

                    // start dialog
                    createCategoryPickerDialog(mCategories);
                }
            }
        });
    }

    private void setupLocationPicker() {
        mLlLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on click, open select location fragment and allow user to choose/alter location
                SelectLocationFragment fragment = new SelectLocationFragment();
                fragment.show(getFragmentManager(), SELECT_LOCATION_FRAGMENT_TAG);
            }
        });
    }

    private void createCategoryPickerDialog(Category[] categories) {
        if (mCategoryDialog == null) {
            mCategoryDialog = CategoryPickerDialog.newInstance(categories);
            mCategoryDialog.setListener(this);
        }
        mCategoryDialog.show(getFragmentManager(), SELECT_CATEGORY_FRAGMENT_TAG);
    }

    private void registerForPostUpdates() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mPostResponse,
                new IntentFilter(EventHandler.BROADCAST_REPORT_RECIEVED));
    }

    private void submit() {
        // Creates a problem using a builder which will validate required inputs
        mBuilder.setUsername(mEtName.getText().toString());
        mBuilder.setPhoneNumber(mEtPhone.getText().toString());
        mBuilder.setCategory(mSelectedCategory);
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
        } else {
            // show minimap
            MapUtils.setStaticMap(mIvLocation, location);
        }
        mBuilder.setLocation(location);
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

    protected Consumer<List<Category>> onCategoriesRetrievedFromDb() {
        return new Consumer<List<Category>>() {
            @Override
            public void accept(List<Category> categories) throws Exception {
                if (categories.size() > 0) {
                    mCategories = categories.toArray(new Category[categories.size()]);
                    mSelectedCategory = mCategories[0];
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
                Toast.makeText(ReportProblemActivity.this,
                        R.string.network_error, Toast.LENGTH_LONG).show();
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
    public void onItemSelected(Category item, int position) {
        if (item == null) {
            return;
        }
        mEtCategory.setText(item.getName());
        mSelectedCategory = item;
    }

    // callbacks for auto location via LocationTracker
    private LocationTracker.LocationListener mAutoLocationListener = new LocationTracker.LocationListener() {

        @Override
        public String getPermissionAlertTitle() {
            return getString(R.string.location_permission_dialog_title);
        }

        @Override
        public String getPermissionAlertDescription() {
            return getString(R.string.location_permission_dialog_description);
        }


        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                updateLocationIcon(false);
                return;
            }
            // We have found the GPS location. Set it.
            setGpsPoint(location);
            // Now, fetch address using geolocation
            FetchAddressIntentService.findAddressWithGoogle(getApplicationContext(), location,
                    new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            // Set address
                            String address = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
                            mEtAddress.setText(address);
                        }
                    });
        }

        @Override
        public void onPermissionDenied() {
            Toast.makeText(getApplicationContext(), R.string.location_permission_denied, Toast.LENGTH_LONG).show();
            updateLocationIcon(true);
        }
    };

    // callback for SelectLocationFragment
    @Override
    public void selectLocation(double lats, double longs, String address) {
        // user manually selected location. disable auto find
        if (mLocationTracker != null) {
            mLocationTracker.onPause();
            mLocationTracker = null;
        }

        // now set address and location using fragment provided location
        if (address != null) {
            mEtAddress.setText(address);
        }
        Location location = new Location(SELECT_LOCATION_FRAGMENT_TAG);
        location.setLatitude(lats);
        location.setLongitude(longs);
        mBuilder.setLocation(location);

        // show mini-map
        MapUtils.setStaticMap(mIvLocation, location);
    }
}
