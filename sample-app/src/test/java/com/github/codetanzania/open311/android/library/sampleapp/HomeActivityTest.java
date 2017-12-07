package com.github.codetanzania.open311.android.library.sampleapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * This tests the two buttons on the sample home activity.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HomeActivityTest {
    private HomeActivity mActivity;

    @Before
    public void startActivity() {
        mActivity = Robolectric.setupActivity(HomeActivity.class);
    }

    @Test
    public void testReportIssueButton() {
        mActivity.findViewById(R.id.btn_report).performClick();
        testActivityStarted("Report Button should launch ReportProblemActivity",
                ReportProblemActivity.class.getName());
    }

    @Test
    public void testListButton() {
        mActivity.findViewById(R.id.btn_list).performClick();
        testActivityStarted("List Button should launch ProblemListActivity",
                ProblemListActivity.class.getName());
    }

    private void testActivityStarted(String message, String expectedActivityName) {
        String startedActivity = shadowOf(mActivity).getNextStartedActivity().getComponent().getClassName();
        assertEquals(message, startedActivity, expectedActivityName);
    }

}
