package com.github.codetanzania.open311.android.library;

import android.Manifest;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.codetanzania.open311.android.library.api.ApiModelConverter;
import com.github.codetanzania.open311.android.library.api.ReportService;
import com.github.codetanzania.open311.android.library.models.Category;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.models.Status;
import com.github.codetanzania.open311.android.library.shadows.ShadowSwipeRefreshLayout;
import com.github.codetanzania.open311.android.library.shadows.ShadowViewPager;
import com.github.codetanzania.open311.android.library.ui.adapters.OpenClosedTabAdapter;
import com.github.codetanzania.open311.android.library.utils.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.github.codetanzania.open311.android.library.api.ReportService.START_FETCH_PROBLEMS_ACTION;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * This tests the ProblemListActivity.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowViewPager.class, ShadowSwipeRefreshLayout.class})
public class ProblemListActivityTest {
    private ProblemListActivity mActivity;
    private ArrayList<Problem> mockProblems;
    private ViewPager mViewPager;
    private FrameLayout mActivityFragmentContainer;
    private FloatingActionButton mFab;

    @Before
    public void startActivity() {
        ShadowApplication.getInstance().grantPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        mActivity = Robolectric.buildActivity(ProblemListActivity.class)
                .create().start().resume().visible().get();

        mActivityFragmentContainer = (FrameLayout) mActivity.findViewById(R.id.frl_fragmentContainer);
        mFab = (FloatingActionButton) mActivity.findViewById(R.id.fab_reportIssue);
    }

    @Test
    public void activityStarted() {
        assertNotNull(mActivityFragmentContainer);
        assertNotNull(mFab);
    }

    @Test
    public void activityStartedLoading() {
        shouldAttemptToGetMyReportedProblems();
        shouldShowLoadingFragment();
    }

    @Test
    public void activityShouldShowErrorFragment() throws InterruptedException {
        sendError();
        shouldShowErrorFragment();
        shouldBeAbleToStartDataReloadFromError();
    }

    @Test
    public void activityShouldShowEmptyFragment() throws InterruptedException {
        sendEmpty();
        shouldShowEmptyFragment();
    }

    @Test
    public void listIsPopulatedWithProblems() throws InterruptedException {
        sendMocks();

        // Tabs should show
        mViewPager = (ViewPager) mActivity.findViewById(R.id.vp_ticketsActivity);
        assertNotNull(mViewPager);
        shouldShowTabFragment();
        tabsShouldBeSortedByStatus();

        // TESTING THIS HERE, AS CAUSES A FALSE NEGATIVE WHEN IN SEPARATE TEST
        // Swipe refresh layout should cause new network call
        ShadowSwipeRefreshLayout swipeRefreshLayout =
                Shadow.extract(mActivity.findViewById(R.id.srf_problem_list));
        swipeRefreshLayout.getOnRefreshListener().onRefresh();
        shouldAttemptToGetMyReportedProblems();
    }

    @Test
    public void fabClickShouldOpenNewReport() {
        mFab.performClick();

        String startedActivity = shadowOf(mActivity)
                .getNextStartedActivity().getComponent().getClassName();
        assertEquals("Fab click should start ReportProblemActivity",
                ReportProblemActivity.class.getName(), startedActivity);
    }

    private void shouldAttemptToGetMyReportedProblems() {
        Intent receivedIntent = shadowOf(mActivity).peekNextStartedService();
        assertNotNull("Started service should not be null", receivedIntent);
        assertEquals("Should start ReportService",
                receivedIntent.getComponent().getClassName(), ReportService.class.getName());
        assertEquals("Should start Fetch of MyReportedProblems",
                receivedIntent.getAction(), START_FETCH_PROBLEMS_ACTION);
        String sentNumber = receivedIntent.getStringExtra(ReportService.FETCH_REQUESTS_INTENT);
        assertEquals("Phone number should be hardcoded",
                "255714095061", sentNumber);
    }

    private void shouldShowLoadingFragment() {
        Fragment fragment = mActivity.getSupportFragmentManager()
                .findFragmentById(R.id.frl_fragmentContainer);
        assertTrue("Should display ProgressBarFragment",
                fragment instanceof ProgressBarFragment);
        ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.pb_activityStatus);
        assertNotNull(progressBar);
    }

    private void shouldShowErrorFragment() {
        Fragment fragment = mActivity.getSupportFragmentManager()
                .findFragmentById(R.id.frl_fragmentContainer);
        assertTrue("Should display ErrorFragment",
                fragment instanceof ErrorFragment);
        assertNotNull("Icon should be shown",mActivity.findViewById(R.id.iv_errorIcon));
        assertNotNull("Message should be shown", mActivity.findViewById(R.id.tv_errorMsg));
    }

    private void shouldBeAbleToStartDataReloadFromError() {
        ImageView ivReload = (ImageView) mActivity.findViewById(R.id.iv_reload);
        assertNotNull("Reload should be shown", ivReload);

        ivReload.performClick();
        shouldAttemptToGetMyReportedProblems();
    }

    private void shouldShowEmptyFragment() {
        Fragment fragment = mActivity.getSupportFragmentManager()
                .findFragmentById(R.id.frl_fragmentContainer);
        assertTrue("Should display EmptyFragment",
                fragment instanceof EmptyListFragment);
        assertNotNull("Icon should be shown",mActivity.findViewById(R.id.iv_emptyIssues));
        assertNotNull("Message should be shown", mActivity.findViewById(R.id.iv_emptyIssues));
    }

    private void shouldShowTabFragment() {
        Fragment fragment = mActivity.getSupportFragmentManager()
                .findFragmentById(R.id.frl_fragmentContainer);

        assertTrue("Should display TabFragment",
                fragment instanceof ProblemTabFragment);
        assertNotNull("Tab bar should be shown",mActivity.findViewById(R.id.tab_layout));
        assertNotNull("View pager should be shown", mActivity.findViewById(R.id.vp_ticketsActivity));

        SwipeRefreshLayout refreshLayout =
                (SwipeRefreshLayout) mActivity.findViewById(R.id.srf_problem_list);
        assertFalse(refreshLayout.isRefreshing());
    }

    // TODO figure out why this doesn't work
//    private void shouldShowAllTab() {
//        mViewPager.setCurrentItem(0);
//
//        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.rv_problem_list);
//        assertNotNull(recyclerView);
//
//        assertEquals("RecyclerView should have 3 items",
//                3, recyclerView.getAdapter().getItemCount());
//
//        RecyclerView.ViewHolder firstRow = recyclerView.findViewHolderForAdapterPosition(0);
//        RecyclerView.ViewHolder secondRow = recyclerView.findViewHolderForAdapterPosition(1);
//        RecyclerView.ViewHolder thirdRow = recyclerView.findViewHolderForAdapterPosition(2);
//        assertNotNull(firstRow);
//        assertNotNull(secondRow);
//        assertNotNull(thirdRow);
//
//        assertListViewMatchesProblem(mockProblems.get(0), firstRow.itemView);
//        assertListViewMatchesProblem(mockProblems.get(1), secondRow.itemView);
//        assertListViewMatchesProblem(mockProblems.get(2), thirdRow.itemView);
//    }

    private void tabsShouldBeSortedByStatus() {
        OpenClosedTabAdapter tabAdapter = (OpenClosedTabAdapter) mViewPager.getAdapter();
        //TODO this is brittle :(
        ProblemListFragment allFrag = (ProblemListFragment) tabAdapter.getItem(0);
        List<Problem> allProblems = allFrag.getArguments().getParcelableArrayList("problems");
        assertEquals("All tab should have 3 items", 3, allProblems.size());
        assertEquals("Should contain full item", mockProblems.get(0), allProblems.get(0));
        assertEquals("Should contain simple puddle", mockProblems.get(1), allProblems.get(1));
        assertEquals("Should contain simple shite", mockProblems.get(2), allProblems.get(2));

        ProblemListFragment openFrag = (ProblemListFragment) tabAdapter.getItem(1);
        List<Problem> openProblems = openFrag.getArguments().getParcelableArrayList("problems");
        assertEquals("Open tab should have 1 item", 1, openProblems.size());
        assertEquals("Should contain simple puddle", mockProblems.get(1), openProblems.get(0));

        ProblemListFragment closedFrag = (ProblemListFragment) tabAdapter.getItem(2);
        List<Problem> closedProblems = closedFrag.getArguments().getParcelableArrayList("problems");
        assertEquals("Closed tab should have 2 items", 2, closedProblems.size());
        assertEquals("Should contain full item", mockProblems.get(0), closedProblems.get(0));
        assertEquals("Should contain simple shite", mockProblems.get(2), closedProblems.get(1));
    }

    private void assertListViewMatchesProblem(Problem problem, View itemView) {
        TextView tvCategoryIcon = (TextView) itemView.findViewById(R.id.tv_categoryIcon);
        TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_problemTitle);
        TextView tvTicketID = (TextView) itemView.findViewById(R.id.tv_problemTicketID);
        TextView tvDescription = (TextView) itemView.findViewById(R.id.tv_problemDescription);
        TextView tvDateCreated = (TextView) itemView.findViewById(R.id.tv_problemDate);

        assertEquals("Left circle should contain category code.", problem.getCategory().getCode(), tvCategoryIcon.getText());
//        assertEquals("Left circle background tint should match status", Color.parseColor(problem.getStatus().getColor(), tvCategoryIcon.getBackground().getColorFilter())); //TODO test tint
        assertEquals("Title should be category name.", problem.getCategory().getName(), tvTitle.getText());
        assertEquals("Ticket number should be correctly displayed", problem.getTicketNumber(), tvTicketID.getText());
        assertEquals("Description should be properly displayed", problem.getDescription(), tvDescription.getText());
        assertEquals("Date created should be properly formatted", DateUtils.formatForDisplay(problem.getCreatedAt()), tvDateCreated.getText());
    }

    private void sendError() {
        EventHandler.errorRetrievingRequests(RuntimeEnvironment.application, new Throwable("Test error"));
    }

    private void sendEmpty() {
        EventHandler.retrievedMyRequests(RuntimeEnvironment.application, new ArrayList<>(), false);
    }

    private void sendMocks() {
        Problem.Builder builder = new Problem.Builder(null);
        Problem fullProblem = ApiModelConverter.convert(ProblemTest.buildMockServerResponse());
        Problem partialProblem1 = builder.buildWithoutValidation(null, null, null,
                null, new Category("Puddle", "123", 3, "PU"),
                null, null, "Magnificent!", "TIC123",
                new Status(true, "In Progress", "#3498DB"), Calendar.getInstance(), null, null, null);
        Problem partialProblem2 = builder.buildWithoutValidation(null, null, null,
                null, new Category("Shite Heap", "456", 3, "SH"),
                null, null, "Eew!", "TIC456",
                new Status(false, "Resolved", "#28B463"), Calendar.getInstance(), null, null, null);
        mockProblems = new ArrayList<>(3);
        mockProblems.add(fullProblem);
        mockProblems.add(partialProblem1);
        mockProblems.add(partialProblem2);

        EventHandler.retrievedMyRequests(RuntimeEnvironment.application, mockProblems, false);
    }
}
