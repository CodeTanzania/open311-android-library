package com.example.majifix311.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.majifix311.EventHandler;
import com.example.majifix311.R;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.models.Status;
import com.example.majifix311.ui.adapters.ProblemListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This is used to show a list of problems.
 */

public class ProblemListFragment extends Fragment implements ProblemListAdapter.OnItemClickListener {
    private final static String PROBLEMS_INTENT = "problems";

    private RecyclerView rvProblems;
    private List<Problem> mProblems;
    private ProblemListAdapter mAdapter;

    // TODO: Use this method to send in problem list on fragment creation
    public static ProblemListFragment getNewInstance(ArrayList<Problem> requests) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PROBLEMS_INTENT, requests);
        ProblemListFragment instance = new ProblemListFragment();
        instance.setArguments(args);
        return instance;
    }

    // TODO: Move this to activity
    private BroadcastReceiver mMyReportedProblemsReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(EventHandler.IS_SUCCESS, false)) {
                mProblems = intent.getParcelableArrayListExtra(EventHandler.PROBLEM_INTENT);
                System.out.println("Problems Received! "+mProblems.size());
                setupRecyclerView();
            } else {
                // TODO Implement error logic
                Toast.makeText(getContext(), "Failure!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_problem_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvProblems = (RecyclerView) view.findViewById(R.id.rv_problem_list);


        // TODO Broadcast management should be handled by activity
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMyReportedProblemsReceived,
                new IntentFilter(EventHandler.BROADCAST_MY_REPORTED_RECIEVED));

        //sendMocks();

        // mProblems = getArguments().getParcelableArrayList(PROBLEMS_INTENT);
        // setupRecyclerView();
    }

//    private void sendMocks() {
//        Problem.Builder builder = new Problem.Builder(null);
//        Problem problem1 = builder.buildWithoutValidation(null, null, null,
//                null, new Category("Puddle", "123", 3, "PU"),
//                null, null, "Magnificent!", "TIC123",
//                new Status(true, "In Progress", "#3498DB"), Calendar.getInstance(), null, null, null);
//        Problem problem2 = builder.buildWithoutValidation(null, null, null,
//                null, new Category("Shite Heap", "456", 3, "SH"),
//                null, null, "Eew!", "TIC456",
//                new Status(false, "Resolved", "#28B463"), Calendar.getInstance(), null, null, null);
//        ArrayList<Problem> problems = new ArrayList<>(2);
//        problems.add(problem1);
//        problems.add(problem2);
//
//        EventHandler.sendMyReportedProblemsList(getContext(), problems);
//    }

    private void setupRecyclerView() {
        mAdapter = new ProblemListAdapter(mProblems, this);
        rvProblems.setAdapter(mAdapter);
        rvProblems.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvProblems.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rvProblems.setHasFixedSize(true); // layout dimens of each item will always be consistent
        rvProblems.setNestedScrollingEnabled(true);
    }

    @Override
    public void onItemClick(Problem problem) {
        // TODO go to item detail on click
        Toast.makeText(getContext(), "Problem "+problem.getTicketNumber()+" has been clicked", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }
}
