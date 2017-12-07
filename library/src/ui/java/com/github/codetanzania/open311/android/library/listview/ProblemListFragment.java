package com.github.codetanzania.open311.android.library.ui.listview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used to show a list of problems.
 */

public class ProblemListFragment extends Fragment implements ProblemListAdapter.OnItemClickListener {
    private final static String PROBLEMS_INTENT = "problems";

    private RecyclerView rvProblems;
    private ProgressBar mProgress;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_problem_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvProblems = (RecyclerView) view.findViewById(R.id.rv_problem_list);
        mProgress = (ProgressBar) view.findViewById(R.id.progress_problem_list);

        if (getArguments() != null) {
            mProblems = getArguments().getParcelableArrayList(PROBLEMS_INTENT);
        }
        if (mProblems != null) {
            setupRecyclerView();
        }
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
        ProblemDetailActivity.startActivity(getActivity(), problem);
    }
}
