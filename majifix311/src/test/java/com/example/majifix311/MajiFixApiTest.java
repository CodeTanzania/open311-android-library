package com.example.majifix311;

import com.example.majifix311.api.MajiFixAPI;
import com.example.majifix311.models.Problem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Dave - Work on 12/1/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MajiFixApiTest {
    private MajiFixAPI mMajiFixAPI = MajiFixAPI.getInstance();

    @Test
    public void problemsByPhoneNumberTest(){
        TestObserver<ArrayList<Problem>> tester =
                mMajiFixAPI.getProblemsByPhoneNumber("255714095061")
                .test();

        assertTrue("Stream didn't terminate", tester.awaitTerminalEvent());
        System.out.println(((ArrayList<Problem>)tester.getEvents().get(0).get(0)).size());
    }
}
