package com.example.majifix311;

import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.db.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 *  This is used for testing database methods.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DatabaseHelperTest {
    private DatabaseHelper mHelper;
    private List<String> mResult;

    @Before
    public void setup() {
        mHelper = new DatabaseHelper(RuntimeEnvironment.application);
        mResult = null;
    }

    @Test
    public void canWriteCategoriesToDatabase() {
        List<ApiService> mockServices = new ArrayList<>(2);
        ApiService mockService1 = new ApiService("1","cat1");
        ApiService mockService2 = new ApiService("2","cat2");
        mockServices.add(mockService1);
        mockServices.add(mockService2);

        ApiServiceGroup group = new ApiServiceGroup();
        group.setServices(mockServices);
        mHelper.setCategories(group, onSavedInDatabase(), onError(), false);

        assertNotNull(mResult);
        assertEquals(2, mResult.size());
        assertEquals("cat1", mResult.get(0));
        assertEquals("cat2", mResult.get(1));
    }

    private Consumer<List<String>> onSavedInDatabase() {
        return new Consumer<List<String>>() {
            @Override
            public void accept(List<String> categories) throws Exception {
                System.out.println("onRetrievedFromNetwork after Database save! "+categories);
                mResult = categories;
                //EventHandler.sendResultReceived(mContext);
            }
        };
    }

    private Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable error) throws Exception {
                System.out.println("onError! "+error);
                mResult = null;
                //EventHandler.sendResultReceived(mContext);
            }
        };
    }
}
