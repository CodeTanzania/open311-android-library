package com.example.majifix311.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.majifix311.models.Attachment;
import com.example.majifix311.utils.AttachmentUtils;

/**
 * This, on click, resizes to show the full image, and on second click goes back to original
 * cropped size. Presently, it needs to be set in an LinearLayout.
 *
 * You can set an attachment directly with 'setAttachment'.
 */

public class ExpandableImageView extends AppCompatImageView {
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

    public void setAttachment(String filePath) {
        if (filePath != null) {
            AttachmentUtils.setPicFromFile(this, filePath);
        }

        // Get height and width of file
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        mBitmapWidth = bmOptions.outWidth;
        mBitmapHeight = bmOptions.outHeight;
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
                new View.OnClickListener() {
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
