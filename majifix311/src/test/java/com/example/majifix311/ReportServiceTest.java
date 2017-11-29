package com.example.majifix311;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import com.example.majifix311.api.ReportService;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.models.Problem;
import com.example.majifix311.models.TaggedProblemList;
import com.example.majifix311.ui.ReportProblemActivity;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static com.example.majifix311.Mocks.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

/**
 * This test ensures that the ReportService works as expected.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReportServiceTest {
    private CountDownLatch mLatch;

    @Before
    public void init() {
        mLatch = new CountDownLatch(1);

//        LocalBroadcastManager.getInstance(RuntimeEnvironment.application).registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                // TODO replace with real logic
//                System.out.println("Broadcast received!");
//                mLatch.countDown();
//            }
//        }, new IntentFilter(EventHandler.BROADCAST_REPORT_RECIEVED));
    }

    @Test
    public void serviceShouldAttemptPost() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(Mocks.VALID_SERVICE_REQUEST_GET));
        server.start();

        MockReportService service = new MockReportService();
        service.setMockEndpoint(server.url("/").toString());
        service.onCreate();
        service.onStartCommand(getServiceIntent(), 0, 0);

        // blocks progress until request
        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);

        // assert request to server was made
        assertNotNull("Server call was made", request);
        assertEquals("Only one server call was made", 1, server.getRequestCount());

        // assert correct information was sent to server
        Gson gson = new Gson();
        ApiServiceRequestPost intent =
                gson.fromJson(request.getBody().readUtf8(), ApiServiceRequestPost.class);
        assertNotNull(intent);
        assertEquals("Username should be correct", mockName, intent.getReporter().getName());
        assertEquals("Phone number should be correct", mockNumber, intent.getReporter().getPhone());
        assertEquals("Only Category Id, as string, should be sent to server", mockCategoryId, intent.getService());
        assertEquals("Latitude should be correct", latitude, intent.getLocation().getLatitude());
        assertEquals("Longitude should be correct", longitude, intent.getLocation().getLongitude());
        assertEquals("Address should be correct", mockAddress, intent.getAddress());
        assertEquals("Description should be correct", mockDescription, intent.getDescription());

        // TODO assert response was handled correctly.
        //assertNotNull(service.getReportedProblem());
    }

    private Intent getServiceIntent() {
        Intent startIntent = new Intent();
        startIntent.setAction(ReportService.START_POST_PROBLEM_ACTION);
        startIntent.putExtra(ReportService.NEW_PROBLEM_INTENT, ProblemTest.buildMockProblem(null));
        return startIntent;
    }

    private class MockReportService extends ReportService {
        String mBaseEndpoint;

        void setMockEndpoint(String endpoint) {
            MajiFix.setBaseEndpoint(endpoint);
        }

//        @Override
//        protected Consumer<Problem> onNext() {
//            return new Consumer<Problem>() {
//                @Override
//                public void accept(Problem problem) throws Exception {
//                    mLatch.countDown();
//                    System.out.println("onNext! "+problem);
//                    mProblem = problem;
//                }
//            };
//        }
    }

    private TestObserver<TaggedProblemList> runTransformer(
            long cacheDelayMs, long serverDelayMs, boolean cacheErrs, boolean serverErrs) {
        Observable<ArrayList<Problem>> empty = Observable.just(new ArrayList<Problem>());
        final Observable<ArrayList<Problem>> network =
                (serverErrs ? errorMaker(new ServerError()) : empty);
        final Observable<ArrayList<Problem>> cache =
                (cacheErrs ? errorMaker(new CacheError()) : empty);

        Consumer<Notification<ArrayList<Problem>>> notifier =
                new Consumer<Notification<ArrayList<Problem>>>() {
            @Override
            public void accept(Notification<ArrayList<Problem>> notification) throws Exception {
                System.out.println(notification + " at " + System.currentTimeMillis());
            }
        };

        ReportService rs = new ReportService();
        System.out.println("cacheDelay: " + cacheDelayMs + ", serverDelay: " + serverDelayMs);
        return network
                .delay(serverDelayMs, TimeUnit.MILLISECONDS)
                .doOnEach(notifier)
                .compose(rs.transform(
                        cache.delay(cacheDelayMs, TimeUnit.MILLISECONDS).doOnEach(notifier),
                        new Function<ArrayList<Problem>, ObservableSource<ArrayList<Problem>>>() {
                            @Override
                            public ObservableSource<ArrayList<Problem>> apply(ArrayList<Problem> problems) throws Exception {
                                return network;
                            }
                        }
                ))
                .test();
    }

    private Predicate<TaggedProblemList> predicate(final boolean isPreliminary) {
        return new Predicate<TaggedProblemList>() {
            @Override
            public boolean test(TaggedProblemList pair) throws Exception {
                return pair.mPreliminary == isPreliminary;
            }
        };
    }

    private <T extends Error> Observable<ArrayList<Problem>> errorMaker(T error) {
        return Observable.error(error);
    }

    private class CacheError extends Error {
    }

    private class ServerError extends Error {
    }

    @SafeVarargs
    private final void compositeStripper(Throwable error, Class<? extends Throwable>... expectedErrors) {
        if (expectedErrors.length == 1 && !(error instanceof CompositeException)) {
            assertEquals("Incorrect Error type", expectedErrors[0], error.getClass());
        } else {
            List<Throwable> innerErrors = ((CompositeException) error).getExceptions();
            assertEquals("Incorrect number of errors", expectedErrors.length,
                    innerErrors.size());
            if(innerErrors.size() == 1) System.out.println("Single Error wrapped in a Composite");
            for (int i = 0; i < innerErrors.size(); i++) {
                assertEquals("Error #" + i + "'s type incorrect", expectedErrors[i],
                        innerErrors.get(i).getClass());
            }
        }
    }

    @Test
    public void testCaseWhereFirstDbSuccessThenServerSuccess() throws IOException, InterruptedException {
        // db should send problems first
        // server should send problems
        TestObserver<TaggedProblemList> test =
                runTransformer(0, 500, false, false);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(2);
        test.assertNoErrors();
        test.assertValueAt(0, predicate(true));
        test.assertValueAt(1, predicate(false));
    }

    @Test
    public void testCaseWhereFirstDbErrorThenServerSuccess() {
        // no need for db error to be sent
        // server should send problems

        TestObserver<TaggedProblemList> test =
                runTransformer(0, 500, true, false);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(1);
        test.assertValueAt(0, predicate(false));
        compositeStripper(test.errors().get(0), CacheError.class);
    }

    @Test
    public void testCaseWhereFirstDbSuccessThenServerError() {
        // db should send problems first
        // server should send error

        TestObserver<TaggedProblemList> test =
                runTransformer(0, 500, false, true);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(1);
        test.assertValueAt(0, predicate(true));
        assertEquals("Multiple base errors emitted",1, test.errorCount());
        compositeStripper(test.errors().get(0), ServerError.class);
        //test.assertError(CompositeException.class);
        //assertEquals(1, test.errors().size());
        //List<Throwable> errors = ((CompositeException) test.errors().get(0)).getExceptions();
        //assertEquals(1, errors.size());
        //assertTrue("Only inner error should be a ServerError", errors.get(0) instanceof ServerError);
    }

    @Test
    public void testCaseWhereFirstDbErrorThenServerError() {
        // no need for db error to be sent
        // server should send error

        TestObserver<TaggedProblemList> test =
                runTransformer(0, 500, true, true);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(0);
        assertEquals("Multiple base errors emitted",1, test.errorCount());
        compositeStripper(test.errors().get(0), CacheError.class, ServerError.class);
        //test.assertError(CompositeException.class);
        //List<Throwable> errors = ((CompositeException) test.errors().get(0)).getExceptions();
        //assertTrue("First error should be a CacheError", errors.get(0) instanceof CacheError);
        //assertTrue("Second error should be a ServerError", errors.get(1) instanceof ServerError);
    }

    @Test
    public void testCaseWhereFirstServerSuccessThenDbSuccess() {
        // server should send problems
        // db does not send problems

        TestObserver<TaggedProblemList> test =
                runTransformer(500, 0, false, false);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(1);
        test.assertValueAt(0, predicate(false));
        test.assertNoErrors();
    }

    @Test
    public void testCaseWhereFirstServerSuccessThenDbError() {
        // server should send problems
        // db does not send error

        TestObserver<TaggedProblemList> test =
                runTransformer(1000, 0, true, false);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(1);
        test.assertValueAt(0, predicate(false));
        test.assertNoErrors();
    }

    @Test
    public void testCaseWhereFirstServerErrorThenDbSuccess() {
        // server should send error
        // db should send problems
        //TODO this functionality needs to match the above, it doesn't.

        TestObserver<TaggedProblemList> test =
                runTransformer(500, 0, false, true);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(0);
        assertEquals("Multiple base errors emitted", 1, test.errorCount());
        compositeStripper(test.errors().get(0), ServerError.class);
        //test.errors().get(0).printStackTrace();
        //List<Throwable> errors = ((CompositeException) test.errors().get(0)).getExceptions();
        //assertEquals(1, errors.size());
        //assertTrue("Single inner error should be a ServerError", errors.get(0) instanceof ServerError);
    }

    @Test
    public void testCaseWhereFirstServerErrorThenDbError() {
        // server should send error
        // db does not need to send error (ui already in error state)

        TestObserver<TaggedProblemList> test =
                runTransformer(500, 0, true, true);

        assertTrue("Stream didn't terminate", test.awaitTerminalEvent());
        test.assertValueCount(0);
        compositeStripper(test.errors().get(0), CacheError.class, ServerError.class);
        assertEquals("Multiple base errors emitted", 1, test.errorCount());
        test.assertError(CompositeException.class);
        //assertEquals(1, test.errors().size());
        //List<Throwable> errors = ((CompositeException) test.errors().get(0)).getExceptions();
        //assertEquals(2, errors.size());
        //assertTrue("First inner error should be a CacheError", errors.get(0) instanceof CacheError);
        //assertTrue("Second inner error should be a ServerError", errors.get(1) instanceof ServerError);
    }
}
