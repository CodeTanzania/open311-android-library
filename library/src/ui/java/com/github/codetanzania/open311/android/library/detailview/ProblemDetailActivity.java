package com.github.codetanzania.open311.android.library.ui.detailview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.codetanzania.open311.android.library.ui.location.MapUtils;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.ui.R;

import java.util.Locale;

public class ProblemDetailActivity extends Activity {
    private static final String PROBLEM_INTENT = "problem";
    private Problem mProblem;

    private Toolbar mToolbar;
    private ImageView mIvMap;
    private ExpandableImageView mIvPhoto;
    private TextView mTvReporter;
    private TextView mTvCategory;
    private TextView mTvLocation;
    private TextView mTvDescription;

    public static void startActivity(Activity callingActivity, Problem problem) {
        Intent intent = new Intent(callingActivity, ProblemDetailActivity.class);
        intent.putExtra(PROBLEM_INTENT, problem);
        callingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);

        // this activity requires a problem to be sent in the intent
        if (getIntent() == null) {
            finish();
            return;
        }
        mProblem = getIntent().getParcelableExtra(PROBLEM_INTENT);

        setToolbar();
        setFab();

        mIvMap = (ImageView) findViewById(R.id.iv_map);
        mIvPhoto = (ExpandableImageView) findViewById(R.id.iv_photo);
        mTvReporter = (TextView) findViewById(R.id.tv_reporter);
        mTvCategory = (TextView) findViewById(R.id.tv_category);
        mTvLocation = (TextView) findViewById(R.id.tv_location);
        mTvDescription = (TextView) findViewById(R.id.tv_description);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProblemData();
    }

    private void setToolbar() {
        // disable default animation so ticket number is always at top
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.ctl_problemDetail);
        layout.setTitleEnabled(false);

        // set title to appbar
        mToolbar = (Toolbar) findViewById(R.id.tb_problemDetail);
        if (mToolbar != null && mProblem.getTicketNumber() != null) {
            mToolbar.setTitle(mProblem.getTicketNumber());
        }
    }

    private void setFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_problemDetail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUser();
            }
        });
    }

    private void setProblemData() {
        setMap();
        setExpandableAttachment();

        // set other data to text views
        if (mProblem.getReporter() != null) {
            String name = mProblem.getReporter().getName();
            String phone = mProblem.getReporter().getPhone();
            String toDisplay = name == null ? phone :
                    name + (phone == null ? "" : "\n+"+phone);

            if (toDisplay != null) {
                mTvReporter.setText(toDisplay);
            }
        }
        String category = mProblem.getCategory() == null ? null : mProblem.getCategory().getName();
        if (category != null) {
            mTvCategory.setText(category);
        }
        if (mProblem.getAddress() != null) {
            mTvLocation.setText(mProblem.getAddress());
        }
        if (mProblem.getDescription() != null) {
            mTvDescription.setText(mProblem.getDescription());
        }
    }

    private void setMap() {
        // add map only when view is measured (so as to get correct sized map from server)
        mIvMap.post(new Runnable() {
            @Override
            public void run() {
                MapUtils.setStaticMap(mIvMap, mProblem.getLocation());
                mIvMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToLocation();
                    }
                });
            }
        });
    }

    private void setExpandableAttachment() {
        // add image only when view is measured (so as to load the correctly sized bitmap from file)
        mIvPhoto.post(new Runnable() {
            @Override
            public void run() {
                if (mProblem.hasAttachments()) {
                    mIvPhoto.setAttachment(mProblem.getAttachments().get(0));
                } else {
                    mIvPhoto.setVisibility(View.GONE);
                }
            }
        });
    }

    private void callUser() {
        // create intent to start phone call
        String phoneNumber = mProblem.getReporter().getPhone();
        Uri phoneUri = Uri.parse("tel:" + phoneNumber);
        String title = String.format(Locale.getDefault(),
                getString(R.string.call_user), mProblem.getReporter().getName(), phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, phoneUri);

        // dispatch intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(intent, title);
            startActivity(chooser);
        }
    }

    private void goToLocation() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="
                +mProblem.getLocation().getLatitude()+ ","+mProblem.getLocation().getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(intent, "Get directions to "+mProblem.getAddress()+":");
            startActivity(chooser);
        }
    }
}
