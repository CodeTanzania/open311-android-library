package com.github.codetanzania.open311.android.library;

import com.github.codetanzania.open311.android.library.api.ApiModelConverter;
import com.github.codetanzania.open311.android.library.api.models.ApiService;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceGroup;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequestGet;
import com.github.codetanzania.open311.android.library.db.DatabaseHelper;
import com.github.codetanzania.open311.android.library.models.Category;
import com.github.codetanzania.open311.android.library.models.Problem;

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
        ApiServiceRequestGet before = Mocks.buildMockServerResponse();
        Problem mockProblem = ApiModelConverter.convert(before);
        problems.add(mockProblem);

        mHelper.saveMyReportedProblems(problems)
                .subscribe(new Consumer<ArrayList<Problem>>() {
                    @Override
                    public void accept(ArrayList<Problem> problems) throws Exception {
                        mProblemResult = problems;
                    }
                });

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
