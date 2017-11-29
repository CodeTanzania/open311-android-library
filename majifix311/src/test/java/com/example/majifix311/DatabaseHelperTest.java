package com.example.majifix311;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 *  This is used for testing database methods.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DatabaseHelperTest {
    private DatabaseHelper mHelper;
    private List<Category> mCategoriesResult;
    private List<Problem> mProblemResult;

    @Before
    public void setup() {
        mHelper = new DatabaseHelper(RuntimeEnvironment.application);
        mCategoriesResult = null;
        mProblemResult = null;
    }

    @Test
    public void canWriteCategoriesToDatabase() {
        List<ApiService> mockServices = new ArrayList<>(2);
        ApiService mockService1 = new ApiService("1","cat1", 1, "one");
        ApiService mockService2 = new ApiService("2","cat2", 2, "two");
        mockServices.add(mockService1);
        mockServices.add(mockService2);

        ApiServiceGroup group = new ApiServiceGroup();
        group.setServices(mockServices);
        mHelper.setCategories(group, onCategoriesSavedInDatabase(), onError(), false);

        assertNotNull(mCategoriesResult);
        assertEquals(2, mCategoriesResult.size());
        // should be sorted
        assertEquals("cat2", mCategoriesResult.get(0).getName());
        assertEquals("2", mCategoriesResult.get(0).getId());
        assertEquals(2, mCategoriesResult.get(0).getPriority());
        assertEquals("two", mCategoriesResult.get(0).getCode());
        assertEquals("cat1", mCategoriesResult.get(1).getName());
        assertEquals("1", mCategoriesResult.get(1).getId());
        assertEquals(1, mCategoriesResult.get(1).getPriority());
        assertEquals("one", mCategoriesResult.get(1).getCode());
    }

    @Test
    public void canWriteProblemsToDatabase() {
        final ArrayList<Problem> problems = new ArrayList<>(1);
        ApiServiceRequestGet before = ProblemTest.buildMockServerResponse();
        Problem mockProblem = ApiModelConverter.convert(before);
        problems.add(mockProblem);

        //TestObserver<ArrayList<Problem>> tester = new TestObserver<>();

        mHelper.saveMyReportedProblems(problems)
                .subscribe(new Consumer<ArrayList<Problem>>() {
                    @Override
                    public void accept(ArrayList<Problem> problems) throws Exception {
                        mProblemResult = problems;
                    }
                });

        //mHelper.saveMyReportedProblems(
        //        serverResponses, onProblemsSavedInDatabase(), onError(), false);

        assertNotNull(mProblemResult);
        ProblemTest.assertGetMatchesMock(mProblemResult.get(0));
    }

    private Consumer<List<Category>> onCategoriesSavedInDatabase() {
        return new Consumer<List<Category>>() {
            @Override
            public void accept(List<Category> categories) throws Exception {
                System.out.println("onRetrievedFromNetwork after Database save! "+categories);
                mCategoriesResult = categories;
                //EventHandler.sendResultReceived(mContext);
            }
        };
    }

    private Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable error) throws Exception {
                System.out.println("onError! "+error);
                mCategoriesResult = null;
                //EventHandler.sendResultReceived(mContext);
            }
        };
    }
}
