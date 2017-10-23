package com.example.majifix311;

import android.os.Parcel;

import com.example.majifix311.models.Reporter;
import com.example.majifix311.models.Status;

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
    private int mockType = Status.OPEN;
    private String mockName = "In Progress";
    private String mockColor = "#000000";

    private Status mockStatus;

    @Before
    public void setup() {
        mockStatus = new Status(true, mockName, mockColor);
    }

    @Test
    public void status_isCreated() {
        assertMatchesMock(mockStatus);
    }

    @Test
    public void isOpen_worksAsExpected() {
        Status open = new Status(true, mockName, mockColor);
        Status closed = new Status(false, mockName, mockColor);
        assertTrue(open.isOpen());
        assertFalse(closed.isOpen());
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
        assertEquals("Type should be correct", mockType, status.getType());
        assertEquals("Name should be correct", mockName, status.getName());
        assertEquals("Color should be correct", mockColor, status.getColor());
    }
}
