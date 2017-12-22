package com.github.codetanzania.open311.android.library;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;

import com.github.codetanzania.open311.android.library.api.ApiModelConverter;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequestGet;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequestPost;
import com.github.codetanzania.open311.android.library.models.Category;
import com.github.codetanzania.open311.android.library.models.ChangeLog;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.utils.AttachmentUtils;
import com.github.codetanzania.open311.android.library.utils.DateUtils;
import com.github.codetanzania.open311.android.library.utils.ProblemCollections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.github.codetanzania.open311.android.library.Mocks.*;
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
                "1", null, null, stoneage, null, null, null, null);
        Problem p2 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, null, skyscrapers, null, null, null, null);
        Problem p3 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, null, pyramids, null, null, null, null);
        Problem p4 = builder.buildWithoutValidation(null, null, null,
                null, null, null, null, null,
                "1", null, null, castles, null, null, null, null);

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

        File file = Mocks.createMockFile();
        builder.addAttachment(file.getAbsolutePath());

        return builder.build();
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

        assertNotNull(post.getAttachments()[0].getName());
        assertNotNull(post.getAttachments()[0].getCaption());
        assertEquals(mockAttachmentMime, post.getAttachments()[0].getMime());
        Bitmap bitmap = AttachmentUtils.decodeFromBase64String(post.getAttachments()[0].getContent());
        assertNotNull(bitmap);
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
        assertEquals("Status boolean should be correct", false, problem.isOpen());
        assertEquals("Priority name should be correct", mockPriorityId, problem.getPriority().getId());
        assertEquals("Priority name should be correct", mockPriorityName, problem.getPriority().getName());
        assertEquals("Priority name should be correct", mockPriorityWeight, problem.getPriority().getWeight());
        assertEquals("Priority name should be correct", mockPriorityColor, problem.getPriority().getColor());

        DateUtilTest.testCalendar(problem.getCreatedAt(), 2015, Calendar.OCTOBER, 22, 9, 3, 46);
        DateUtilTest.testCalendar(problem.getUpdatedAt(), 2016, Calendar.OCTOBER, 22, 9, 3, 46);
        DateUtilTest.testCalendar(problem.getResolvedAt(), 2017, Calendar.OCTOBER, 22, 9, 3, 46);

        assertChangelogsMatch(problem);
    }

    public static void assertChangelogsMatch(Problem problem) {
        assertEquals("There should be one changelog", 4, problem.getChangeLog().size());

        for (ChangeLog log : problem.getChangeLog()) {
            assertEquals("Changelog changer name should be correct",
                    mockChangelogChangerName, log.getChanger().getName());
            assertEquals("Changelog changer phone should be correct",
                    mockChangelogChangerPhone, log.getChanger().getPhone());
            assertEquals("Changelog changer email should be correct",
                    mockChangelogChangerEmail, log.getChanger().getEmail());
            assertEquals("Changelog date created should be correct",
                    DateUtils.getCalendarFromMajiFixApiString(mockChangelogDateCreatedString), log.getCreatedAt());
            assertEquals("Changelog is public should be correct",
                    mockChangelogIsPublic, log.isPublic());
        }

        ChangeLog statusLog = problem.getChangeLog().get(0);
        assertEquals("Changelog status name should be correct",
                mockChangelogStatusName, statusLog.getStatus().getName());
        assertEquals("Changelog status color should be correct",
                mockChangelogStatusColor, statusLog.getStatus().getColor());
        assertEquals("Changelog status id should be correct",
                mockChangelogStatusId, statusLog.getStatus().getId());

        ChangeLog commentLog = problem.getChangeLog().get(1);
        assertEquals("Changelog comment should be correct",
                mockChangelogComment, commentLog.getComment());

        ChangeLog assigneeLog = problem.getChangeLog().get(2);
        assertEquals("Changelog assignee name be correct",
                mockChangelogAssigneeName, assigneeLog.getAssignee().getName());
        assertEquals("Changelog assignee name be correct",
                mockChangelogAssigneePhone, assigneeLog.getAssignee().getPhone());
        assertEquals("Changelog assignee name be correct",
                mockChangelogAssigneeEmail, assigneeLog.getAssignee().getEmail());

        ChangeLog priorityLog = problem.getChangeLog().get(3);
        assertEquals("Changelog priority name should be correct",
                mockChangelogPriorityName, priorityLog.getPriority().getName());
        assertEquals("Changelog priority weight should be correct",
                mockChangelogPriorityWeight, priorityLog.getPriority().getWeight());
        assertEquals("Changelog priority color should be correct",
                mockChangelogPriorityColor, priorityLog.getPriority().getColor());
        assertEquals("Changelog priority id should be correct",
                mockChangePriorityId, priorityLog.getPriority().getId());
    }

    private static void assertOneAttachmentMatches(Problem problem) {
        assertEquals("There should be one attachment", 1, problem.getAttachments().size());
        Bitmap bitmap = AttachmentUtils.getScaledBitmap(problem.getAttachments().get(0), 125, 125);
        assertNotNull("Bitmap should be saved at attachment url", bitmap);
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
