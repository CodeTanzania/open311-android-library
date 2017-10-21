package com.example.majifix311;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.majifix311.api.ReportService;
import com.example.majifix311.models.Problem;
import com.example.majifix311.ui.ReportProblemActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * This tests the ReportProblemActivity form.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReportProblemActivityTest {
    private ReportProblemActivity mActivity;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mCategoryView;
    private TextView mLocationError;
    private EditText mAddressView;
    private EditText mDescriptionView;
    private Button mSubmitButton;

    private String mockName = "Test User";
    private String mockNumber = "123456789";
    private String mockCategory = "5968b64148dfc224bb47748d";
    private Location mockLocation = new Location("");
    private String mockAddress = "55 Marimbo St";
    private String mockDescription = "Horrible horrible horrible!!";

    @Before
    public void startActivity() {
        mActivity = Robolectric.setupActivity(ReportProblemActivity.class);

        mNameView = (EditText) mActivity.findViewById(R.id.et_name);
        mPhoneView = (EditText) mActivity.findViewById(R.id.et_phone);
        mCategoryView = (EditText) mActivity.findViewById(R.id.et_category);
        mLocationError = (TextView) mActivity.findViewById(R.id.tv_location_error);
        mAddressView = (EditText) mActivity.findViewById(R.id.et_address);
        mDescriptionView = (EditText) mActivity.findViewById(R.id.et_description);
        mSubmitButton = (Button) mActivity.findViewById(R.id.btn_submit);
    }

    @Test
    public void fieldsShouldBePresent() {
        assertNotNull(mActivity);

        assertNotNull(mNameView);
        assertNotNull(mPhoneView);
        assertNotNull(mCategoryView);
        assertNotNull(mLocationError);
        assertNotNull(mAddressView);
        assertNotNull(mDescriptionView);
        assertNotNull(mSubmitButton);
    }

    @Test
    public void errorMessagesAreShown() {
        TextInputLayout tilName = (TextInputLayout) mActivity.findViewById(R.id.til_name);
        TextInputLayout tilNumber = (TextInputLayout) mActivity.findViewById(R.id.til_phone);
//        TextInputLayout tilCategory = (TextInputLayout) mActivity.findViewById(R.id.til_category);
        TextInputLayout tilAddress = (TextInputLayout) mActivity.findViewById(R.id.til_address);
        TextInputLayout tilDescription = (TextInputLayout) mActivity.findViewById(R.id.til_description);

        setFieldsAndSubmit(null, null, null, null, null, null);
        assertNotNull(tilName.getError());
        assertNotNull(tilNumber.getError());
//        assertNotNull(tilCategory.getError());
        assertEquals(View.VISIBLE, mLocationError.getVisibility());
        assertNotNull(tilAddress.getError());
        assertNotNull(tilDescription.getError());

        setFieldsAndSubmit(null, mockNumber, null, mockLocation, null, mockDescription);
        assertNull(tilNumber.getError());
        assertNull(tilDescription.getError());
//        assertNotNull(tilCategory.getError());
        assertEquals(View.INVISIBLE, mLocationError.getVisibility());
        assertNotNull(tilName.getError());
        assertNotNull(tilAddress.getError());
    }

    @Test
    public void submitTriggersReportService() {
        setFieldsAndSubmit(mockName, mockNumber, mockCategory,
                mockLocation, mockAddress, mockDescription);

        Intent receivedIntent = shadowOf(mActivity).getNextStartedService();
        assertNotNull("Started service should not be null", receivedIntent);
        assertEquals("Should start ReportService",
                receivedIntent.getComponent().getClassName(), ReportService.class.getName());

        Problem sent = receivedIntent.getParcelableExtra(ReportService.NEW_PROBLEM_INTENT);
        assertNotNull("Newly created problem should be sent to service", sent);
        assertEquals("Name should be correct", sent.getUsername(), mockName);
        assertEquals("Phone should be correct", sent.getPhoneNumber(), mockNumber);
        assertEquals("Category should be correct", sent.getCategory(), mockCategory);
        assertEquals("Latitude should be correct", sent.getLocation().getLatitude(), mockLocation.getLatitude());
        assertEquals("Longitude should be correct", sent.getLocation().getLongitude(), mockLocation.getLongitude());
        assertEquals("Address should be correct", sent.getAddress(), mockAddress);
        assertEquals("Description should be correct", sent.getDescription(), mockDescription);
    }

    @Test
    public void isRegisteredForBroadcasts() {
        // TODO Test error

        // TODO Add dialog
        EventHandler.sendReportReceived(RuntimeEnvironment.application, null);
        assertTrue(mActivity.isFinishing());
    }

    private void setFieldsAndSubmit(String username, String phone, String category,
                                    Location location, String address, String description) {
        mNameView.setText(username);
        mPhoneView.setText(phone);
        mCategoryView.setText(category);
        mActivity.setGpsPoint(location);
        mAddressView.setText(address);
        mDescriptionView.setText(description);

        mSubmitButton.performClick();
    }


}
