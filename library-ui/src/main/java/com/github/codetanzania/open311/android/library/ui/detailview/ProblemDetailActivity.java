package com.github.codetanzania.open311.android.library.ui.detailview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.codetanzania.open311.android.library.auth.Auth;
import com.github.codetanzania.open311.android.library.models.ChangeLog;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.ui.R;
import com.github.codetanzania.open311.android.library.ui.location.MapUtils;
import com.github.codetanzania.open311.android.library.utils.AttachmentUtils;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Locale;

public class ProblemDetailActivity extends Activity {
    private static final String PROBLEM_INTENT = "problem";
    private Problem mProblem;

    private Toolbar mToolbar;
    private ImageView mIvMap;
    private ExpandableImageView mIvPhoto;
    private TextView mTvReporter;
    private TextView mTvCategory;
    private TextView mTvStatus;
    private TextView mTvPriority;
    private TextView mTvLocation;
    private TextView mTvDescription;
    private MessagesList mMlChangelog;

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

        mIvMap = findViewById(R.id.iv_map);
        mIvPhoto = findViewById(R.id.iv_photo);
        mTvReporter = findViewById(R.id.tv_reporter);
        mTvCategory = findViewById(R.id.tv_category);
        mTvStatus = findViewById(R.id.tv_status);
        mTvPriority = findViewById(R.id.tv_priority);
        mTvLocation = findViewById(R.id.tv_location);
        mTvDescription = findViewById(R.id.tv_description);
        mMlChangelog = findViewById(R.id.ml_changelogs);

        setProblemData();
    }

    private void setToolbar() {
        // disable default animation so ticket number is always at top
        CollapsingToolbarLayout layout = findViewById(R.id.ctl_problemDetail);
        layout.setTitleEnabled(false);

        // set title to appbar
        mToolbar = findViewById(R.id.tb_problemDetail);
        if (mToolbar != null && mProblem.getTicketNumber() != null) {
            mToolbar.setTitle(mProblem.getTicketNumber());
        }
    }

    private void setFab() {
        FloatingActionButton fab = findViewById(R.id.fab_problemDetail);
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
        String category = mProblem.getCategory() == null
                ? null : mProblem.getCategory().getName();
        if (category != null) {
            mTvCategory.setText(category);
        }
        String status = mProblem.getStatus() == null
                ? null : mProblem.getStatus().getName();
        if (status != null) {
            mTvStatus.setText(status);
        }
        String priority = mProblem.getPriority() == null
                ? null : mProblem.getPriority().getName();
        if (priority != null) {
            mTvPriority.setText(priority);
        }
        if (mProblem.getAddress() != null) {
            mTvLocation.setText(mProblem.getAddress());
        }
        if (mProblem.getDescription() != null) {
            mTvDescription.setText(mProblem.getDescription());
        }

        setChangelog();
//        setTempSpinners();
    }

//    private void setTempSpinners() {
//        Spinner spinner = (Spinner) findViewById(R.id.status);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.status_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapter);
//        spinner = (Spinner) findViewById(R.id.priority);
//        adapter = ArrayAdapter.createFromResource(this,
//                R.array.priority_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//        spinner = (Spinner) findViewById(R.id.assign);
//        adapter = ArrayAdapter.createFromResource(this,
//                R.array.assignee_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//    }

    private void setChangelog() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String base64Avatar) {
                if (base64Avatar != null) {
                    // If the party contains an avatar, use it
                    Bitmap bitmap = AttachmentUtils.decodeFromBase64String(base64Avatar);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        return;
                    }
                }
                // else use default
                imageView.setImageResource(R.drawable.ic_default_avatar);
            }
        };

        // TODO base this on ability to edit flag
        String senderId = Auth.getInstance().isLogin() ? Auth.getInstance().getParty().getObjectId() : "1";
        MessagesListAdapter<ChatLog> adapter = new MessagesListAdapter<>(senderId, imageLoader);
        mMlChangelog.setAdapter(adapter);
        for (ChangeLog log : mProblem.getChangeLog()) {
            adapter.addToStart(new ChatLog(log), true);
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
