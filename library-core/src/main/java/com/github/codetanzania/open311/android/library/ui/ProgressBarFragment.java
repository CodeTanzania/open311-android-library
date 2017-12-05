package com.github.codetanzania.open311.android.library.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.codetanzania.open311.android.library.R;


public class ProgressBarFragment extends Fragment {

    private static ProgressBarFragment mSelf;

    public static ProgressBarFragment getInstance() {
        if (mSelf == null) {
            mSelf = new ProgressBarFragment();
        }
        return mSelf;
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_loading, group, false);
    }
}
