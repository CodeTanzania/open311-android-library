package com.example.majifix311;

import com.example.majifix311.api.MajiFixAPI;
import com.example.majifix311.api.models.ApiServiceRequestGetMany;
import com.example.majifix311.models.Problem;
import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Dave - Work on 12/1/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MajiFixApiTest {

    @Test
    public void problemsByPhoneNumberTest() throws IOException{
        int responses = 42;
        String[] mock = Mocks.generateProblemQueryResponse(responses);

        MockWebServer server = new MockWebServer();
        for (int i = 0; i < mock.length; i++) {
            server.enqueue(new MockResponse().setBody(mock[i]));
        }
        server.start();

        MajiFix.setBaseEndpoint(server.url("/").toString());

        TestObserver<ArrayList<Problem>> tester =
                MajiFixAPI.getInstance().getProblemsByPhoneNumber("8675309")
                .test();

        assertTrue("Stream didn't terminate", tester.awaitTerminalEvent());
        assertEquals("Should get out as many problems as were requested",
                responses,
                ((ArrayList<Problem>)tester.getEvents().get(0).get(0)).size());
        assertEquals("Made the wrong number of server calls", mock.length, server.getRequestCount());
    }


}
