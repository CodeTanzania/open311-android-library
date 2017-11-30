package com.example.majifix311.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.majifix311.EventHandler;
import com.example.majifix311.R;
import com.example.majifix311.api.ReportService;
import com.example.majifix311.models.Problem;
import com.example.majifix311.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * This activity shows a list of reported issues.
 */

public class ProblemListActivity extends AppCompatActivity implements ErrorFragment.OnReloadClickListener {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NONE, EMPTY, LOADING, SUCCESS, ERROR})
    public @interface UiState {}
    public static final String NONE = "none";
    public static final String EMPTY = "empty";
    public static final String LOADING = "loading";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    @Utils.UiState
    private String mUiState = Utils.NONE;
    private int mFragmentContainerRes = R.id.frl_fragmentContainer;
    private FloatingActionButton mFab;

    private BroadcastReceiver mMyReportedProblemsReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(EventHandler.IS_SUCCESS, false)) {
                ArrayList<Problem> problems = intent.getParcelableArrayListExtra(EventHandler.REQUEST_LIST);
                boolean isPreliminary = intent.getBooleanExtra(EventHandler.IS_PRELIMINARY_DATA,false);
                if (problems == null || problems.isEmpty()) {
                    if (!isPreliminary) {
                        showEmptyFragment();
                    }
                } else {
                    showListTabs(problems);
                    if (isPreliminary) {
                        // TODO show spinner
                    }
                }
            } else {
                showErrorFragment();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mFab = (FloatingActionButton) findViewById(R.id.fab_reportIssue);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startReportIntent =
                        new Intent(ProblemListActivity.this, ReportProblemActivity.class);
                startActivity(startReportIntent);
            }
        });

        fetchMyReportedProblems();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMyReportedProblemsReceived);
        super.onDestroy();
    }

    private void fetchMyReportedProblems() {
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(mMyReportedProblemsReceived,
                new IntentFilter(EventHandler.BROADCAST_MY_PROBLEMS_FETCHED));

        ReportService.fetchProblems(getBaseContext(),"255714095061");

        // only show loading fragment if issues are not currently shown
        if (!mUiState.equals(Utils.SUCCESS)) {
            showLoadingFragment();
        }
    }

    /* show or hide menu items */ //TODO Implement
    private void showMenuItems(boolean show) {
        //showMenu = show;
        // trigger call to `onCreateOptionsMenu`
        invalidateOptionsMenu();
    }

    private void showLoadingFragment() {
        switchOutFragment(Utils.LOADING, false, new ProgressBarFragment());
    }

    private void showErrorFragment() {
        switchOutFragment(Utils.ERROR, false, new ErrorFragment());

        // show toast to inform user that there was a server error
        Toast.makeText(this, R.string.error_server, Toast.LENGTH_LONG).show();
    }

    private void showEmptyFragment() {
        switchOutFragment(Utils.EMPTY, false, new EmptyListFragment());
    }

    private void showListTabs(ArrayList<Problem> problems) {
        // show service requests grouped into tabs
        boolean isNew = switchOutFragment(
                Utils.SUCCESS, true, ProblemTabFragment.getNewInstance(problems));

        if (!isNew) {
            // if items are already displayed, just update data
            ProblemTabFragment tabFragment = (ProblemTabFragment) getSupportFragmentManager()
                    .findFragmentByTag(Utils.SUCCESS);
            if (tabFragment != null) {
                tabFragment.updateProblems(problems);
            }
        }
    }

    private boolean switchOutFragment(@Utils.UiState String state, boolean showMenu, Fragment fragment) {
        if (mUiState.equals(state)) {
            return false;
        }

        showMenuItems(showMenu);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(mFragmentContainerRes, fragment, state)
                .disallowAddToBackStack()
                .commitAllowingStateLoss();

        mUiState = state;
        return true;
    }


    @Override
    public void onReloadClicked() {
        fetchMyReportedProblems();
    }
}
