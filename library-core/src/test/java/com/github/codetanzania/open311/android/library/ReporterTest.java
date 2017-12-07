package com.github.codetanzania.open311.android.library;

import android.os.Parcel;

import com.github.codetanzania.open311.android.library.api.ApiModelConverter;
import com.github.codetanzania.open311.android.library.api.models.ApiReporter;
import com.github.codetanzania.open311.android.library.models.Reporter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * This tests that reporters are parsed (etc) correctly.
 */

@RunWith(RobolectricTestRunner.class)
public class ReporterTest {
    private String mockName = "Sam";
    private String mockNumber = "69";
    private String mockEmail = "a@b.com";
    private String mockAccount = "123";

    private Reporter mockReporter;

    @Before
    public void setup() {
        mockReporter = new Reporter();
        mockReporter.setName(mockName);
        mockReporter.setPhone(mockNumber);
        mockReporter.setEmail(mockEmail);
        mockReporter.setAccount(mockAccount);
    }

    @Test
    public void reporter_isParcelable() throws IOException {
        Parcel parcel = Parcel.obtain();
        mockReporter.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Reporter fromParcel = Reporter.CREATOR.createFromParcel(parcel);
        assertMatchesMock(fromParcel);
    }

    @Test
    public void reporter_isConvertedForApiUse() {
        ApiReporter apiReporter = new ApiReporter(mockReporter);

        assertEquals("Username should be parsed correctly", mockName, apiReporter.getName());
        assertEquals("Phone number should be parsed correctly", mockNumber, apiReporter.getPhone());
        assertEquals("Email should be parsed correctly", mockEmail, apiReporter.getEmail());
        assertEquals("Account Number should be parsed correctly", mockAccount, apiReporter.getAccount());
    }

    @Test
    public void reporter_isConvertedForAppUse() {
        ApiReporter apiReporter = new ApiReporter(mockReporter);

        Reporter afterConversion = ApiModelConverter.convert(apiReporter);
        assertMatchesMock(afterConversion);
    }

    private void assertMatchesMock(Reporter reporter) {
        assertEquals("Username should be parsed correctly", mockName, reporter.getName());
        assertEquals("Phone number should be parsed correctly", mockNumber, reporter.getPhone());
        assertEquals("Email should be parsed correctly", mockEmail, reporter.getEmail());
        assertEquals("Account Number should be parsed correctly", mockAccount, reporter.getAccount());
    }
}
