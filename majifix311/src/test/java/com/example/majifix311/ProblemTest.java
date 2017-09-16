package com.example.majifix311;

import android.location.Location;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * This is used to test the problem model.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProblemTest implements Problem.Builder.InvalidCallbacks {
    private String mockName = "Test User";
    private String mockNumber = "123456789";
    private String mockCategory = "Leakage";
    private Location mockLocation = new Location("");
    private double latitude = 1.1d;
    private double longitude = 2.2d;
    private String mockAddress = "55 Marimbo St";
    private String mockDescription = "Horrible horrible horrible!!";

    private int problemCount;

    @Before
    public void prepare() {
        problemCount = 0;
        mockLocation.setLatitude(latitude);
        mockLocation.setLongitude(longitude);
    }

    @Test
    public void builderCanCreateProblem() {
        Problem result = buildMockProblem();

        assertNotNull(result);
        assertEquals("All inputs should be valid", 0, problemCount);

        assertEquals("Username should be correct", mockName, result.getUsername());
        assertEquals("Phone number should be correct", mockNumber, result.getPhoneNumber());
        assertEquals("Category should be correct", mockCategory, result.getCategory());
        assertEquals("Latitude should be correct", latitude, result.getLocation().getLatitude());
        assertEquals("Longitude should be correct", longitude, result.getLocation().getLongitude());
        assertEquals("Address should be correct", mockAddress, result.getAddress());
        assertEquals("Description should be correct", mockDescription, result.getDescription());
    }

    @Test
    public void testConvertFromProblemToApiServiceRequest() {
        Problem before = buildMockProblem();
        ApiServiceRequestPost after = ApiModelConverter.convert(before);
        assertEquals(mockName, after.getReporter().getName());
        assertEquals(mockNumber, after.getReporter().getPhone());
        assertEquals(mockCategory, after.getService());
        assertEquals(latitude, after.getLocation().getLatitude());
        assertEquals(longitude, after.getLocation().getLongitude());
        assertEquals(mockAddress, after.getAddress());
        assertEquals(mockDescription, after.getDescription());
    }


    @Test
    public void testConvertFromProblemToApiServiceRequestHandlesNulls() {
        Problem before = null;
        ApiServiceRequestPost after = ApiModelConverter.convert(before);
        assertNull(after);

        before = buildMockProblem();
        before.setLocation(null);
        after = ApiModelConverter.convert(before);
        assertEquals(mockName, after.getReporter().getName());
        assertNull(after.getLocation());
        assertEquals(mockDescription, after.getDescription());
    }

    @Test
    public void testConvertFromApiServiceRequestToProblem() {
        ApiServiceRequestGet before = buildMockServerResponse();
        Problem after = ApiModelConverter.convert(before);
        assertEquals(mockName, after.getUsername());
        assertEquals(mockNumber, after.getPhoneNumber());
        assertEquals(mockCategory, after.getCategory());
        assertEquals(latitude, after.getLocation().getLatitude());
        assertEquals(longitude, after.getLocation().getLongitude());
        assertEquals(mockAddress, after.getAddress());
        assertEquals(mockDescription, after.getDescription());
    }


    @Test
    public void testConvertFromApiServiceRequestToProblemHandlesNulls() {
        ApiServiceRequestGet before = null;
        Problem after = ApiModelConverter.convert(before);
        assertNull(after);

        before = buildMockServerResponse();
        before.setLocation(null);
        after = ApiModelConverter.convert(before);
        assertEquals(mockName, after.getUsername());
        assertNull(after.getLocation());
        assertEquals(mockDescription, after.getDescription());
    }

    @Test
    public void requiredFieldCallbacksAreFired() {
        Problem.Builder builder = new Problem.Builder(this);
        Problem result = builder.build();

        assertNull(result);
        assertEquals("All inputs should be valid", 6, problemCount);
    }

    private Problem buildMockProblem() {
        Problem.Builder builder = new Problem.Builder(this);
        builder.setUsername(mockName);
        builder.setPhoneNumber(mockNumber);
        builder.setCategory(mockCategory);
        builder.setLocation(mockLocation);
        builder.setAddress(mockAddress);
        builder.setDescription(mockDescription);
        return builder.build();
    }

    private ApiServiceRequestGet buildMockServerResponse() {
        return new ApiServiceRequestGet(mockName, mockNumber,
                mockCategory, latitude, longitude, mockAddress, mockDescription);
    }

    @Override
    public void onInvalidUsername() {
        problemCount++;
    }

    @Override
    public void onInvalidPhoneNumber() {
        problemCount++;
    }

    @Override
    public void onInvalidCategory() {
        problemCount++;
    }

    @Override
    public void onInvalidLocation() {
        problemCount++;
    }

    @Override
    public void onInvalidAddress() {
        problemCount++;
    }

    @Override
    public void onInvalidDescription() {
        problemCount++;
    }
}
