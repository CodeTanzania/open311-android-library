package com.example.majifix311.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.majifix311.R;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Problem;
import com.example.majifix311.utils.AttachmentUtils;
import com.example.majifix311.utils.MapUtils;

public class ProblemDetailActivity extends Activity {
    private static final String PROBLEM_INTENT = "problem";
    private Problem mProblem;

    private Toolbar mToolbar;
    private ImageView mIvMap;
    private ImageView mIvPhoto;
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

        mToolbar = (Toolbar) findViewById(R.id.tb_problemDetail);
        mIvMap = (ImageView) findViewById(R.id.iv_map);
        mIvPhoto = (ImageView) findViewById(R.id.iv_photo);
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

    private void setProblemData() {
        // title to appbar
        if (mToolbar != null && mProblem.getTicketNumber() != null) {
            mToolbar.setTitle(mProblem.getTicketNumber());
        }
        // map and photos to slider
        System.out.println("map size:"+mIvMap.getWidth() + mIvMap.getHeight());
        MapUtils.setStaticMap(mIvMap, mProblem.getLocation());
        mIvMap.post(new Runnable() {
            @Override
            public void run() {
                MapUtils.setStaticMap(mIvMap, mProblem.getLocation());
            }
        });
        System.out.println("has attachments:"+mProblem.hasAttachments());
        if (mProblem.hasAttachments()) {
            System.out.println("has attachments:"+mProblem.getAttachments().get(0).getMime());
            Attachment attachment = mProblem.getAttachments().get(0);
            if (attachment != null) {
                Bitmap photo = AttachmentUtils.decodeFromBase64String(attachment.getContent());
                if (photo != null) {
                    mIvPhoto.setImageBitmap(photo);
                }
            }
        }

        // rest to text views
        if (mProblem.getReporter() != null) {
            String name = mProblem.getReporter().getName();
            String phone = mProblem.getReporter().getPhone();
            String toDisplay = name == null ? phone :
                    name + (phone == null ? "" : "\n"+phone);

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
}
