package com.github.codetanzania.open311.android.library.ui.listview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.codetanzania.open311.android.library.auth.Auth;
import com.github.codetanzania.open311.android.library.ui.auth.SecureCompactActivity;
import com.github.codetanzania.open311.android.library.ui.report.ReportProblemActivity;
import com.github.codetanzania.open311.android.library.ui.state.EmptyListFragment;
import com.github.codetanzania.open311.android.library.ui.state.ErrorFragment;
import com.github.codetanzania.open311.android.library.EventHandler;
import com.github.codetanzania.open311.android.library.api.ReportService;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.ui.R;
import com.github.codetanzania.open311.android.library.ui.state.ProgressBarFragment;
import com.github.codetanzania.open311.android.library.utils.AttachmentUtils;
import com.github.codetanzania.open311.android.library.utils.Flags;

import java.util.ArrayList;

/**
 * This activity shows a list of reported issues.
 */

public class ProblemListActivity extends SecureCompactActivity implements ErrorFragment.OnReloadClickListener {

    @Flags.UiState
    private String mUiState = Flags.NONE;
    private int mFragmentContainerRes = R.id.frl_fragmentContainer;
    private SwipeRefreshLayout mRefreshLayout;
    private FloatingActionButton mFab;

    private BroadcastReceiver mMyReportedProblemsReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPreliminary = intent.getBooleanExtra(EventHandler.IS_PRELIMINARY_DATA,false);
            if (intent.getBooleanExtra(EventHandler.IS_SUCCESS, false)) {
                ArrayList<Problem> problems = intent.getParcelableArrayListExtra(EventHandler.REQUEST_LIST);
                if (problems == null || problems.isEmpty()) {
                    if (!isPreliminary) {
                        showEmptyFragment();
                    }
                } else {
                    mRefreshLayout.setRefreshing(isPreliminary);
                    showListTabs(problems);
                }
            } else {
                mRefreshLayout.setRefreshing(isPreliminary);
                showErrorFragment();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srf_problem_list);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMyReportedProblems();
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab_reportIssue);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startReportIntent =
                        new Intent(ProblemListActivity.this, ReportProblemActivity.class);
                startActivity(startReportIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMyReportedProblems();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (AttachmentUtils.permissionGranted(requestCode, grantResults)) {
            fetchMyReportedProblems();
        } else {
            Toast.makeText(getBaseContext(), R.string.permission_required_list_view, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMyReportedProblemsReceived);
        super.onDestroy();
    }

    private void fetchMyReportedProblems() {
        if (AttachmentUtils.hasPermissions(this)) {
            LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(mMyReportedProblemsReceived,
                    new IntentFilter(EventHandler.BROADCAST_MY_PROBLEMS_FETCHED));

            // As this is a secure activity, Party should never be null at this point
            String phone = Auth.getInstance().getParty().getPhone();
            ReportService.fetchProblems(getBaseContext(), phone);

            // only show loading fragment if issues are not currently shown
            if (!mUiState.equals(Flags.SUCCESS)) {
                showLoadingFragment();
            }
        } else {
            AttachmentUtils.requestPermissions(this);
        }
    }

    /* show or hide menu items */ //TODO Implement
    private void showMenuItems(boolean show) {
        //showMenu = show;
        // trigger call to `onCreateOptionsMenu`
        invalidateOptionsMenu();
    }

    private void showLoadingFragment() {
        switchOutFragment(Flags.LOADING, false, new ProgressBarFragment());
    }

    private void showErrorFragment() {
        // only show error fragment if list is not already loaded
        if (!mUiState.equals(Flags.SUCCESS)) {
            switchOutFragment(Flags.ERROR, false, new ErrorFragment());
        }

        // show toast to inform user that there was a server error
        Toast.makeText(this, R.string.error_server, Toast.LENGTH_LONG).show();
    }

    private void showEmptyFragment() {
        switchOutFragment(Flags.EMPTY, false, new EmptyListFragment());
    }

    private void showListTabs(ArrayList<Problem> problems) {
        // show service requests grouped into tabs
        boolean isNew = switchOutFragment(
                Flags.SUCCESS, true, ProblemTabFragment.getNewInstance(problems));

        if (!isNew) {
            // if items are already displayed, just update data
            ProblemTabFragment tabFragment = (ProblemTabFragment) getSupportFragmentManager()
                    .findFragmentByTag(Flags.SUCCESS);
            if (tabFragment != null) {
                tabFragment.updateProblems(problems);
            }
        }
    }

    private boolean switchOutFragment(@Flags.UiState String state, boolean showMenu, Fragment fragment) {
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
