package com.example.majifix311;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.example.majifix311.api.ReportService;
import com.example.majifix311.ui.ReportProblemActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.List;
import java.util.concurrent.CountDownLatch;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

/**
 * This test ensures that the ReportService works as expected.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReportServiceTest {
    private CountDownLatch mLatch;
    private ShadowContentResolver resolver;

    @Before
    public void init(){
//        ContentResolver resolver = RuntimeEnvironment.application.getContentResolver();
//        ShadowContentResolver shadowResolver = shadowOf(resolver);
//
//        mLatch = new CountDownLatch(1);
    }

    @Test
    public void setInputs() {
        String mockName = "Test User";
        String mockNumber = "123456789";
        String mockAddress = "55 Marimbo St";
        String mockDescription = "Horrible horrible horrible!!";

        MockReportService service = new MockReportService();
//        service.setUsername(mockName);
//        service.setNumber(mockNumber);
//        service.setAddress(mockAddress);
//        service.setDescription(mockDescription);
//        service.
    }


    @Test
    public void serviceShouldVerifyInputs() throws InterruptedException {
//        Intent startIntent = new Intent(ReportProblemActivity.this, ReportService.class);
//        startIntent.setAction(ReportService.STARTFOREGROUND_ACTION);
//        startService(startIntent);
//        Intent intent = new Intent( RuntimeEnvironment.application, MockReportService.class );
//        MockReportService service = new MockReportService();
//        service.onCreate();
//        service.onHandleIntent(intent);
//
//        LocalBroadcastManager.getInstance(RuntimeEnvironment.application).registerReceiver(
//                new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        mLatch.countDown();
//                    }
//                }, new IntentFilter(ReportService.BROADCAST_ACTION));
//
//        mLatch.await();
//        fail();
    }

    // observerable = data stream
    // observer = subscribe to data stream
    // schedulers = which thread to run on

    public class MockReportService extends ReportService {
//        public void onHandleIntent(Intent intent ){
//            super.onHandleIntent( intent );
//            //mLatch.countDown();
//        }
//
//        @Override
//        public void sendResult() {
//            Intent resultIntent = new Intent();
//            resultIntent.setAction(BROADCAST_ACTION);
//            resultIntent.putExtra("data","All is well in the world!");
//            LocalBroadcastManager.getInstance(RuntimeEnvironment.application).sendBroadcast(resultIntent);
//        }
    }

    private void onServiceReturned() {
        mLatch.countDown();
    }
}
