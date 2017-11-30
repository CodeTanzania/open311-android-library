package com.example.majifix311.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.majifix311.R;
import com.example.majifix311.models.Problem;
import com.example.majifix311.ui.ProblemListFragment;
import com.example.majifix311.utils.ProblemCollections;

import java.util.ArrayList;

public class OpenClosedTabAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;

    private ArrayList<Problem> all;
    private ArrayList<Problem> open;
    private ArrayList<Problem> closed;

    private final Context mContext;

    private boolean isEmpty;

    public OpenClosedTabAdapter(
            Context ctx, FragmentManager fm, ArrayList<Problem> requests) {
        super(fm);

        this.mContext = ctx;

        if (requests.isEmpty()) {
            isEmpty = true;
            return;
        }

//        This can be used to mock a CLOSED request for testing purposes on list view
//        ServiceRequest mockRequest = new Problem();
//        mockRequest.description = "mock desc";
//        mockRequest.service = new Service("B", "Billing", "#FFFFFF");
//        mockRequest.createdAt = new Date(2017, 10, 3, 12, 19);
//        mockRequest.status = new Status(CLOSED, "#000000");
//        requests.add(mockRequest);

        all = new ArrayList<>();
        open = new ArrayList<>();
        closed = new ArrayList<>();

        ProblemCollections.sortByDate(all);
        for (Problem request : requests) {
            all.add(request);

            if (request.getStatus().isOpen()) {
                open.add(request);
            } else {
                closed.add(request);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (isEmpty) {
            return ProblemListFragment.getNewInstance(null);
        }

        switch (position) {
            case 0: return ProblemListFragment.getNewInstance(all);
            case 1: return ProblemListFragment.getNewInstance(open);
            case 2: return ProblemListFragment.getNewInstance(closed);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (isEmpty) {
            return null;
        }
        switch (position) {
            case 0: return mContext.getString(R.string.tab_all);
            case 1: return mContext.getString(R.string.tab_open);
            case 2: return mContext.getString(R.string.tab_closed);
        }
        return null;
    }

    @Override
    public int getCount() {
        return isEmpty ? 1 : NUM_ITEMS;
    }
}
