package com.example.majifix311;

import android.location.Location;

import com.example.majifix311.BuildConfig;
import com.example.majifix311.Problem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * This is used to test reporting.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProblemTest implements Problem.Builder.InvalidCallbacks {
    private String mockName = "Test User";
    private String mockNumber = "123456789";
    private String mockCategory = "Leakage";
    private Location mockLocation = new Location("");
    private String mockAddress = "55 Marimbo St";
    private String mockDescription = "Horrible horrible horrible!!";

    private int problemCount;

    @Before
    public void prepare() {
        problemCount = 0;
    }

    @Test
    public void builderCanCreateProblem() {
        Problem.Builder builder = new Problem.Builder(this);
        builder.setUsername(mockName);
        builder.setPhoneNumber(mockNumber);
        builder.setCategory(mockCategory);
        builder.setLocation(mockLocation);
        builder.setAddress(mockAddress);
        builder.setDescription(mockDescription);
        Problem result = builder.build();

        assertNotNull(result);
        assertEquals("All inputs should be valid", 0, problemCount);

        assertEquals("Username should be correct", mockName, result.getUsername());
        assertEquals("Phone number should be correct", mockNumber, result.getPhoneNumber());
        assertEquals("Address should be correct", mockAddress, result.getAddress());
        assertEquals("Description should be correct", mockDescription, result.getDescription());
    }

    @Test
    public void requiredFieldCallbacksAreFired() {
        Problem.Builder builder = new Problem.Builder(this);
        Problem result = builder.build();

        assertNull(result);
        assertEquals("All inputs should be valid", 6, problemCount);
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
