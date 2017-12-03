package com.example.majifix311.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.example.majifix311.R;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.utils.AttachmentUtils;

/**
 * This is a drag and drop element that will, when clicked, show a dialog with options to either
 * start a camera or select from gallery.
 *
 * To have the view handle the intent correctly, please call `button.displayOnActivityResult()`
 * and `button.onRequestPermissionResult()` in the Activity lifecycle methods. This will ensure
 * that the AttachmentUrl is properly set, and the thumbnail is properly displayed in the
 * 'mPreview' ImageView.
 *
 * Currently, it is possible to customize the layout used, via xml with `app:ab_layout`.
 * To ensure the preview works as expected, also be sure to also provide `app:ab_preview_id`,
 * or use the same id as used by the default layout (`R.id.iv_add_photo`).
 *
 */

public class AttachmentButton extends LinearLayout {
    private int mLayoutRes;
    private int mPreviewImageViewId;

    private String mPendingImageUrl;

    private ImageView mPreview;

    public AttachmentButton(Context context) {
        super(context);
        init();
    }

    public AttachmentButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setCustomAttributes(context, attrs);
        init();
    }

    public AttachmentButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomAttributes(context, attrs);
        init();
    }

    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.AttachmentButton, 0, 0);
        try {
            mLayoutRes = a.getInteger(R.styleable.AttachmentButton_ab_layout, R.layout.view_add_attachment);
            mPreviewImageViewId = a.getInteger(R.styleable.AttachmentButton_ab_preview_id, R.id.iv_add_photo);
        } finally {
            a.recycle();
        }
    }

    public String getAttachmentUrl() {
        return mPendingImageUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        mPendingImageUrl = attachmentUrl;
    }

    public boolean displayOnActivityResult(int requestCode, int resultCode, Intent data) {
        return AttachmentUtils.setThumbnailFromActivityResult(mPreview, mPendingImageUrl,
                                                       requestCode, resultCode, data);
    }

    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults) {
        mPendingImageUrl = AttachmentUtils.onRequestPermissionResult(
                                    getActivity(), requestCode, permissions, grantResults);
    }

    private void init() {
        inflate(getContext(),mLayoutRes, this);
        setOrientation(VERTICAL);

        LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setGravity(TEXT_ALIGNMENT_CENTER);
        }

        mPreview = (ImageView) findViewById(mPreviewImageViewId);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), AttachmentButton.this);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.attachmenttype, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Activity activity = getActivity();
                        if (activity != null) {
                            if (item.getItemId() == R.id.action_start_gallery) {
                                AttachmentUtils.dispatchAddFromGalleryIntent(activity);
                            } else if (item.getItemId() == R.id.action_start_camera) {
                                mPendingImageUrl = AttachmentUtils.dipatchTakePictureIntent(activity);
                            }
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
