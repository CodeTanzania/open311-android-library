package com.github.codetanzania.open311.android.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.codetanzania.open311.android.library.R;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.ui.adapters.OpenClosedTabAdapter;

import java.util.ArrayList;

/**
 * This fragment contains a view pager which can be used to switch between "all",
 * "open" and "closed" issues.
 */
public class ProblemTabFragment extends Fragment {
    private static final String SERVICE_REQUESTS = "service_requests";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ArrayList<Problem> mProblems;
    private OpenClosedTabAdapter mAdapter;

    public static ProblemTabFragment getNewInstance(ArrayList<Problem> requests) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(SERVICE_REQUESTS, requests);
        ProblemTabFragment instance = new ProblemTabFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_ticketsActivity);
        mProblems = getArguments()
                .getParcelableArrayList(SERVICE_REQUESTS);
        createAdapter(mProblems);

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void updateProblems(ArrayList<Problem> updatedRequests) {
        // TODO make this smarter!
        createAdapter(updatedRequests);
    }

    private void createAdapter(ArrayList<Problem> newRequests) {
        mAdapter = new OpenClosedTabAdapter(
                getActivity(),
                getChildFragmentManager(),
                mProblems);

        mViewPager.setAdapter(mAdapter);
    }
}
