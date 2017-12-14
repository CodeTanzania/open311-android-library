package com.github.codetanzania.open311.android.library.ui.detailview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.github.codetanzania.open311.android.library.utils.AttachmentUtils;

/**
 * This, on click, resizes to show the full image, and on second click goes back to original
 * cropped size. Presently, it needs to be set in an LinearLayout.
 *
 * You can set an attachment directly with 'setAttachment'.
 */

public class ExpandableImageView extends AppCompatImageView {
    private String mFilepath;
    private int mBitmapHeight;
    private int mBitmapWidth;

    private int mWidth;
    private int mCollapsedHeight;
    private int mExpandedHeight;

    public ExpandableImageView(Context context) {
        super(context);
        init();
    }

    public ExpandableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAttachment(final String filePath) {
        mFilepath = filePath;

        // when view is laid out
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // display given attachment
                        displayUrl();
                    }
        });
    }

    private void displayUrl() {
        if (mFilepath == null) {
            return;
        }
        AttachmentUtils.setPicFromFile(ExpandableImageView.this, mFilepath);

        // Get height and width of file
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFilepath, bmOptions);
        mBitmapWidth = bmOptions.outWidth;
        mBitmapHeight = bmOptions.outHeight;
        if (mBitmapHeight == 0) {
            setVisibility(GONE);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            setVisibility(View.GONE);
        } else {
            mBitmapWidth = bitmap.getWidth();
            mBitmapHeight = bitmap.getHeight();
            setImageBitmap(bitmap);
        }
    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
        setOnClickListener(
                new OnClickListener() {
                    boolean isExpanded;

                    @Override
                    public void onClick(View v) {
                        if (mCollapsedHeight == 0) {
                            mCollapsedHeight = v.getHeight();
                            mWidth = v.getWidth();
                        }
                        if (isExpanded) {
                            setLayoutParams(new LinearLayout.LayoutParams(
                                    mWidth, mCollapsedHeight));
                        } else {
                            resizeImageView();
                        }
                        isExpanded = !isExpanded;
                    }
                });
        setVisibility(GONE);
    }

    /** This resizes the image so that aspect ratio of photo is maintained */
    public void resizeImageView() {
        if (mExpandedHeight == 0) {
            int intendedWidth = getWidth();
            float scale = (float) intendedWidth / mBitmapWidth;
            mExpandedHeight = Math.round(mBitmapHeight * scale);
        }

        // Resizes imageview TODO make generic for more than LinearLayout
        setLayoutParams(new LinearLayout.LayoutParams(mWidth, mExpandedHeight));
    }
}
