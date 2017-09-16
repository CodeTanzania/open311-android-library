package com.example.majifix311;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.example.majifix311.api.ReportService;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;
import com.example.majifix311.ui.ReportProblemActivity;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static com.example.majifix311.ProblemTest.latitude;
import static com.example.majifix311.ProblemTest.longitude;
import static com.example.majifix311.ProblemTest.mockAddress;
import static com.example.majifix311.ProblemTest.mockCategory;
import static com.example.majifix311.ProblemTest.mockDescription;
import static com.example.majifix311.ProblemTest.mockNumber;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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
    public void init(){
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
        assertEquals("Only one server call was made", 1,server.getRequestCount());

        // assert correct information was sent to server
        Gson gson = new Gson();
        ApiServiceRequestPost intent =
                gson.fromJson(request.getBody().readUtf8(), ApiServiceRequestPost.class);
        assertNotNull(intent);
        assertEquals("Username should be correct", ProblemTest.mockName, intent.getReporter().getName());
        assertEquals("Phone number should be correct", mockNumber, intent.getReporter().getPhone());
        assertEquals("Category should be correct", mockCategory, intent.getService());
        assertEquals("Latitude should be correct", latitude, intent.getLocation().getLatitude());
        assertEquals("Longitude should be correct", longitude, intent.getLocation().getLongitude());
        assertEquals("Address should be correct", mockAddress, intent.getAddress());
        assertEquals("Description should be correct", mockDescription, intent.getDescription());

        // TODO assert response was handled correctly.
        //assertNotNull(service.getReportedProblem());
    }

    private Intent getServiceIntent() {
        Intent startIntent = new Intent();
        startIntent.setAction(ReportService.STARTFOREGROUND_ACTION);
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
}
