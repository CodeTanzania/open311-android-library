package com.example.majifix311;

import android.location.Location;
import android.os.Parcel;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiServiceRequest;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.models.Reporter;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static com.example.majifix311.Mocks.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * This is used to test the problem model.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProblemTest implements Problem.Builder.InvalidCallbacks {
    private static Category mockCategory = new Category(mockCategoryName, mockCategoryId);
    private static Location mockLocation = new Location("");
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

        assertPostMatchesMock(result);
    }

    private void assertPostMatchesMock(Problem problem) {
        assertEquals("Username should be correct", mockName, problem.getUsername());
        assertEquals("Phone number should be correct", mockNumber, problem.getPhoneNumber());
        assertEquals("Email should be correct", mockEmail, problem.getEmail());
        assertEquals("Account should be correct", mockAccount, problem.getAccount());
        assertEquals("Category should be correct", mockCategoryName, problem.getCategory().getName());
        assertEquals("Category should be correct", mockCategoryId, problem.getCategory().getId());
        assertEquals("Latitude should be correct", latitude, problem.getLocation().getLatitude());
        assertEquals("Longitude should be correct", longitude, problem.getLocation().getLongitude());
        assertEquals("Address should be correct", mockAddress, problem.getAddress());
        assertEquals("Description should be correct", mockDescription, problem.getDescription());
    }

    private void assertGetMatchesMock(Problem problem) {
        assertPostMatchesMock(problem);
        assertEquals("Ticket number should be correct", mockTicketNumber, problem.getTicketNumber());
        assertEquals("Status name should be correct", mockStatusName, problem.getStatus().getName());
        assertEquals("Status color should be correct", mockStatusColor, problem.getStatus().getColor());

        DateUtilTest.testCalendar(problem.getCreatedAt(), 2015, Calendar.OCTOBER, 22, 9, 3, 46);
        DateUtilTest.testCalendar(problem.getUpdatedAt(), 2016, Calendar.OCTOBER, 22, 9, 3, 46);
        DateUtilTest.testCalendar(problem.getResolvedAt(), 2017, Calendar.OCTOBER, 22, 9, 3, 46);

        //assertEquals("Attachments should be correct", );
        //assertEquals("Comments should be correct", );
    }

    private void assertApiServiceRequestMatchesMock(ApiServiceRequestPost post) {
        assertEquals(mockName, post.getReporter().getName());
        assertEquals(mockNumber, post.getReporter().getPhone());
        assertEquals(mockEmail, post.getReporter().getEmail());
        assertEquals(mockAccount , post.getReporter().getAccount());
        assertEquals(mockCategory.getId(), post.getService());
        assertEquals(latitude, post.getLocation().getLatitude());
        assertEquals(longitude, post.getLocation().getLongitude());
        assertEquals(mockAddress, post.getAddress());
        assertEquals(mockDescription, post.getDescription());
    }

    @Test
    public void testConvertFromProblemToApiServiceRequest() {
        Problem before = buildMockProblem(this);
        ApiServiceRequestPost after = ApiModelConverter.convert(before);
        assertApiServiceRequestMatchesMock(after);
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

        assertGetMatchesMock(after);
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

    @Test
    public void testParcelProblemToPost() {
        Problem original = buildMockProblem(this);
        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Problem fromParcel = Problem.CREATOR.createFromParcel(parcel);
        assertPostMatchesMock(fromParcel);
    }

    @Test
    public void testParcelProblemOnGet() {
        ApiServiceRequestGet before = buildMockServerResponse();
        Problem original = ApiModelConverter.convert(before);

        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Problem fromParcel = Problem.CREATOR.createFromParcel(parcel);
        assertGetMatchesMock(fromParcel);
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
        ApiServiceRequestGet fromJson = new Gson().fromJson(VALID_SERVICE_REQUEST_GET, ApiServiceRequestGet.class);
        assertNotNull(fromJson);

        return fromJson;
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
