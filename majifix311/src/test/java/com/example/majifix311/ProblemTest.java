package com.example.majifix311;

import android.location.Location;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.models.Reporter;

import org.junit.Before;
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
    static String mockName = "Test User";
    static String mockNumber = "123456789";
    static String mockEmail = "a@b.com";
    static String mockAccount = "A123";
    static Category mockCategory = new Category("Puddle","5968b64148dfc224bb47748d");
    static Location mockLocation = new Location("");
    static double latitude = 1.1d;
    static double longitude = 2.2d;
    static String mockAddress = "55 Marimbo St";
    static String mockDescription = "Horrible horrible horrible!!";

    private int problemCount;

    @Before
    public void prepare() {
        problemCount = 0;
    }

    @Test
    public void builderCanCreateProblem() {
        Problem result = buildMockProblem(this);

        assertNotNull(result);
        assertEquals("All inputs should be valid", 0, problemCount);

        assertEquals("Username should be correct", mockName, result.getUsername());
        assertEquals("Phone number should be correct", mockNumber, result.getPhoneNumber());
        assertEquals("Email should be correct", mockEmail, result.getEmail());
        assertEquals("Account should be correct", mockAccount, result.getAccount());
        assertEquals("Category should be correct", mockCategory, result.getCategory());
        assertEquals("Latitude should be correct", latitude, result.getLocation().getLatitude());
        assertEquals("Longitude should be correct", longitude, result.getLocation().getLongitude());
        assertEquals("Address should be correct", mockAddress, result.getAddress());
        assertEquals("Description should be correct", mockDescription, result.getDescription());
    }

    @Test
    public void testConvertFromProblemToApiServiceRequest() {
        Problem before = buildMockProblem(this);
        ApiServiceRequestPost after = ApiModelConverter.convert(before);
        assertEquals(mockName, after.getReporter().getName());
        assertEquals(mockNumber, after.getReporter().getPhone());
        assertEquals(mockEmail, after.getReporter().getEmail());
        assertEquals(mockAccount , after.getReporter().getAccount());
        assertEquals(mockCategory.getId(), after.getService());
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

        before = buildMockProblem(this);
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
        assertEquals(mockEmail, after.getEmail());
        assertEquals(mockAccount, after.getAccount());
        assertEquals(mockCategory.getName(), after.getCategory().getName());
        assertEquals(mockCategory.getId(), after.getCategory().getId());
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

    public static Problem buildMockProblem(Problem.Builder.InvalidCallbacks listener) {
        Problem.Builder builder = new Problem.Builder(listener);
        builder.setUsername(mockName);
        builder.setPhoneNumber(mockNumber);
        builder.setEmail(mockEmail);
        builder.setAccountNumber(mockAccount);
        builder.setCategory(mockCategory);
        mockLocation.setLatitude(latitude);
        mockLocation.setLongitude(longitude);
        builder.setLocation(mockLocation);
        builder.setAddress(mockAddress);
        builder.setDescription(mockDescription);
        return builder.build();
    }

    private ApiServiceRequestGet buildMockServerResponse() {
        Reporter mockReporter = new Reporter();
        mockReporter.setName(mockName);
        mockReporter.setPhone(mockNumber);
        mockReporter.setEmail(mockEmail);
        mockReporter.setAccount(mockAccount);

        return new ApiServiceRequestGet(mockReporter,
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
