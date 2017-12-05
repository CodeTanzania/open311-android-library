package com.example.majifix311.integration;

import android.content.Context;

import com.example.majifix311.utils.SharedPrefsUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertTrue;

/**
 * SharedPrefsUtils Tests
 *
 * @author lally elias
 */

@Config(sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class SharedPrefsUtilsTest {

    private Context context;

    @Before
    public void setup() {
        context = ShadowApplication.getInstance().getApplicationContext();
    }

    @Test
    public void testShouldBeAbleToSetString() {

        String key = "ANY_STRING";
        String value = "Any";

        Boolean isSet = SharedPrefsUtils.set(context, key, value);
        String setValue = SharedPrefsUtils.get(context, key, "");

        assertTrue("String should be set", isSet);
        assertTrue("String should be same value", setValue.equals(value));

    }

    @Test
    public void testShouldBeAbleToSetFloat() {
        String key = "ANY_FLOAT";
        Float value = 1.9f;

        Boolean isSet = SharedPrefsUtils.set(context, key, value);
        Float setValue = SharedPrefsUtils.get(context, key, 0f);

        assertTrue("String should be set", isSet);
        assertTrue("String should be same value", setValue.equals(value));

    }

    @Test
    public void testShouldBeAbleToSetLong() {

        String key = "ANY_LONG";
        Long value = 19L;

        Boolean isSet = SharedPrefsUtils.set(context, key, value);
        Long setValue = SharedPrefsUtils.get(context, key, 0L);

        assertTrue("String should be set", isSet);
        assertTrue("String should be same value", setValue.equals(value));

    }

    @Test
    public void testShouldBeAbleToSetInteger() {

        String key = "ANY_INT";
        Integer value = 19;

        Boolean isSet = SharedPrefsUtils.set(context, key, value);
        Integer setValue = SharedPrefsUtils.get(context, key, 0);

        assertTrue("String should be set", isSet);
        assertTrue("String should be same value", setValue.equals(value));

    }

    @Test
    public void testShouldBeAbleToSetBoolean() {

        String key = "ANY_BOOL";
        Boolean value = true;

        Boolean isSet = SharedPrefsUtils.set(context, key, value);
        Boolean setValue = SharedPrefsUtils.get(context, key, false);

        assertTrue("String should be set", isSet);
        assertTrue("String should be same value", setValue.equals(value));

    }
}