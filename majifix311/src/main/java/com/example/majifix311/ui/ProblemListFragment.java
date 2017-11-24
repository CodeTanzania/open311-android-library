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
import com.example.majifix311.api.ReportService;
import com.example.majifix311.models.Problem;
import com.example.majifix311.ui.adapters.ProblemListAdapter;

import java.util.ArrayList;
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
                mProblems = intent.getParcelableArrayListExtra(EventHandler.REQUEST_LIST);
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
                new IntentFilter(EventHandler.BROADCAST_MY_PROBLEMS_FETCHED));

        ReportService.fetchProblems(getContext(),"255714095061");
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(mMyReportedProblemsReceived);
        super.onDestroy();
    }

    private void setupRecyclerView() {
        if (getActivity() != null && isAdded()) {
            mAdapter = new ProblemListAdapter(mProblems, this);
            rvProblems.setAdapter(mAdapter);
            rvProblems.setLayoutManager(
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvProblems.addItemDecoration(
                    new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            rvProblems.setHasFixedSize(true); // layout dimens of each item will always be consistent
            rvProblems.setNestedScrollingEnabled(true);
        }
    }

    @Override
    public void onItemClick(Problem problem) {
        // TODO go to item detail on click
        Toast.makeText(getContext(), "Problem "+problem.getTicketNumber()+" has been clicked", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }
}
