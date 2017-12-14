package com.github.codetanzania.open311.android.library;

import android.os.Parcel;

import com.github.codetanzania.open311.android.library.models.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * This tests that status is parsed (etc) correctly.
 */

@RunWith(RobolectricTestRunner.class)
public class StatusTest {
    private String mockId = "123";
    private String mockName = "In Progress";
    private String mockColor = "#000000";

    private Status mockStatus;

    @Before
    public void setup() {
        mockStatus = new Status(mockId, mockName, mockColor);
    }

    @Test
    public void status_isCreated() {
        assertMatchesMock(mockStatus);
    }

    @Test
    public void status_isParcelable() {
        Parcel parcel = Parcel.obtain();
        mockStatus.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Status fromParcel = Status.CREATOR.createFromParcel(parcel);
        assertMatchesMock(fromParcel);
    }

    private void assertMatchesMock(Status status) {
        assertEquals("Type should be correct", mockId, status.getId());
        assertEquals("Name should be correct", mockName, status.getName());
        assertEquals("Color should be correct", mockColor, status.getColor());
    }
}
