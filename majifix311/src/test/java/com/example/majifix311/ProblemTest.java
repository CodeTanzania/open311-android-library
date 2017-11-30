package com.example.majifix311;

import android.location.Location;
import android.os.Parcel;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.utils.ProblemCollections;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.majifix311.Mocks.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * This is used to test the problem model.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProblemTest implements Problem.Builder.InvalidCallbacks {
    private static Category mockCategory = new Category(
            mockCategoryName, mockCategoryId, 0, mockCategoryCode);
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

    @Test
    public void verifyEmailIsCorrect() {
        String valid = "kri@gm.com";
        String invalid = "kkjsf!";
        Problem.Builder builder = new Problem.Builder(this);
        assertTrue(builder.isValidEmail(valid));
        assertFalse(builder.isValidEmail(invalid));
    }

    @Test
    public void problemsAreSortedByDate() {
        Problem.Builder builder = new Problem.Builder(null);

        Calendar stoneage = Calendar.getInstance();
        stoneage.setTimeInMillis(100);
        Calendar pyramids = Calendar.getInstance();
        pyramids.setTimeInMillis(200);
        Calendar castles = Calendar.getInstance();
        castles.setTimeInMillis(300);
        Calendar skyscrapers = Calendar.getInstance();
        skyscrapers.setTimeInMillis(400);

        Problem p1 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, stoneage, null, null, null);
        Problem p2 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, skyscrapers, null, null, null);
        Problem p3 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, pyramids, null, null, null);
        Problem p4 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, castles, null, null, null);

        List<Problem> problems = new ArrayList<>(2);
        problems.add(p1);
        problems.add(p2);
        problems.add(p3);
        problems.add(p4);

        ProblemCollections.sortByDate(problems);

        assertEquals(p2, problems.get(0));
        assertEquals(p4, problems.get(1));
        assertEquals(p3, problems.get(2));
        assertEquals(p1, problems.get(3));
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

        Attachment attachment = new Attachment(mockAttachmentTitle, mockAttachmentCaption, mockAttachmentMime, mockAttachmentContent);
        builder.addAttachment(attachment);

        return builder.build();
    }

    public static ApiServiceRequestGet buildMockServerResponse() {
        ApiServiceRequestGet fromJson = new Gson().fromJson(VALID_SERVICE_REQUEST_GET, ApiServiceRequestGet.class);
        assertNotNull(fromJson);

        return fromJson;
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

        assertEquals(mockAttachmentTitle, post.getAttachments()[0].getName());
        assertEquals(mockAttachmentCaption, post.getAttachments()[0].getCaption());
        assertEquals(mockAttachmentMime, post.getAttachments()[0].getMime());
        assertEquals(mockAttachmentContent, post.getAttachments()[0].getContent());
    }

    public static void assertPostMatchesMock(Problem problem) {
        assertEquals("Username should be correct", mockName, problem.getUsername());
        assertEquals("Phone number should be correct", mockNumber, problem.getPhoneNumber());
        assertEquals("Email should be correct", mockEmail, problem.getEmail());
        assertEquals("Account should be correct", mockAccount, problem.getAccount());
        assertEquals("Category name should be correct", mockCategoryName, problem.getCategory().getName());
        assertEquals("Category id should be correct", mockCategoryId, problem.getCategory().getId());
        assertEquals("Category priority should be correct", 0, problem.getCategory().getPriority());
        assertEquals("Category code should be correct", mockCategoryCode, problem.getCategory().getCode());
        assertEquals("Latitude should be correct", latitude, problem.getLocation().getLatitude());
        assertEquals("Longitude should be correct", longitude, problem.getLocation().getLongitude());
        assertEquals("Address should be correct", mockAddress, problem.getAddress());
        assertEquals("Description should be correct", mockDescription, problem.getDescription());

        assertOneAttachmentMatches(problem);
    }

    public static void assertGetMatchesMock(Problem problem) {
        assertPostMatchesMock(problem);
        assertEquals("Ticket number should be correct", mockTicketNumber, problem.getTicketNumber());
        assertEquals("Status name should be correct", mockStatusName, problem.getStatus().getName());
        assertEquals("Status color should be correct", mockStatusColor, problem.getStatus().getColor());
        assertEquals("Status boolean should be correct", false, problem.getStatus().isOpen());

        DateUtilTest.testCalendar(problem.getCreatedAt(), 2015, Calendar.OCTOBER, 22, 9, 3, 46);
        DateUtilTest.testCalendar(problem.getUpdatedAt(), 2016, Calendar.OCTOBER, 22, 9, 3, 46);
        DateUtilTest.testCalendar(problem.getResolvedAt(), 2017, Calendar.OCTOBER, 22, 9, 3, 46);
    }

    private static void assertOneAttachmentMatches(Problem problem) {
        assertEquals("There should be one attachment", 1, problem.getAttachments().size());
        Attachment attachment = problem.getAttachments().get(0);
        assertEquals("Attachment name should be correct", Mocks.mockAttachmentTitle, attachment.getName());
        assertEquals("Attachment caption should be correct", Mocks.mockAttachmentCaption, attachment.getCaption());
        assertEquals("Attachment mime should be correct", Mocks.mockAttachmentMime, attachment.getMime());
        assertEquals("Attachment content should be correct", Mocks.mockAttachmentContent, attachment.getContent());
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
