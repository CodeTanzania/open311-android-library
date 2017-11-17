package com.example.majifix311;

import android.app.Application;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.models.Status;
import com.example.majifix311.ui.ProblemListActivity;
import com.example.majifix311.ui.ProblemListFragment;
import com.example.majifix311.utils.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * This tests the ProblemListActivity.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProblemListActivityTest {
    private ProblemListActivity mActivity;
    private ArrayList<Problem> mockProblems;
    private RecyclerView mRecyclerView;

    @Before
    public void startActivity() {
        mActivity = Robolectric.setupActivity(ProblemListActivity.class);
        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.rv_problem_list);
    }

    @Test
    public void fragmentStarted() {
        assertNotNull(mRecyclerView);
    }

    @Test
    public void listIsPopulatedWithProblems() {
        sendMocks();

        RecyclerView.ViewHolder firstRow = mRecyclerView.findViewHolderForAdapterPosition(0);
        RecyclerView.ViewHolder secondRow = mRecyclerView.findViewHolderForAdapterPosition(1);
        RecyclerView.ViewHolder thirdRow = mRecyclerView.findViewHolderForAdapterPosition(2);
        assertNotNull(firstRow);
        assertNotNull(secondRow);
        assertNotNull(thirdRow);

        assertListViewMatchesProblem(mockProblems.get(0), firstRow.itemView);
        assertListViewMatchesProblem(mockProblems.get(1), secondRow.itemView);
        assertListViewMatchesProblem(mockProblems.get(2), thirdRow.itemView);
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
        mockProblems = new ArrayList<>(2);
        mockProblems.add(fullProblem);
        mockProblems.add(partialProblem1);
        mockProblems.add(partialProblem2);

        EventHandler.sendMyReportedProblemsList(RuntimeEnvironment.application, mockProblems);
    }

}
