package com.github.codetanzania.open311.android.library.ui.state;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.codetanzania.open311.android.library.ui.R;

/**
 * This is used when user has never reported an issue.
 */

public class EmptyListFragment extends Fragment {
    public static final String ERROR_MSG = "ERROR_MSG";

    public static EmptyListFragment getNewInstance(int messageRes) {
        EmptyListFragment instance = new EmptyListFragment();
        Bundle args = new Bundle();
        args.putInt(ERROR_MSG, messageRes);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // bind data to the components
        Bundle bundle = getArguments();
        String errMsg;
        if (bundle != null) {
            errMsg = bundle.getString(ERROR_MSG, getString(R.string.error_no_reported));
            TextView emptyMessage = (TextView) view.findViewById(R.id.tv_emptyIssues);
            emptyMessage.setText(errMsg);
        }
    }
}
