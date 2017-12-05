package com.github.codetanzania.open311.android.library.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.github.codetanzania.open311.android.library.R;
import com.github.codetanzania.open311.android.library.models.Category;

/**
 * This is used to pick between categories/services.
 */

public class CategoryPickerDialog extends DialogFragment {
    private static final String OPTIONS_EXTRA = "list";
    private int mSelected = 0;
    private Category[] mOptions;
    private String[] mTitles;
    private OnItemSelected mListener;

    public static CategoryPickerDialog newInstance(Category[] options) {
        CategoryPickerDialog dialog = new CategoryPickerDialog();
        Bundle args = new Bundle();
        args.putParcelableArray(OPTIONS_EXTRA, options);
        dialog.setArguments(args);
        return dialog;
    }

    public void setListener(OnItemSelected listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.hint_category)
                .setPositiveButton(R.string.action_select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Category selected: "+which);
                        if (mListener != null) {
                            mListener.onItemSelected(mOptions[mSelected], mSelected);
                        }
                    }
                })
                .setNegativeButton(R.string.action_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Dialog canceled");
                    }
                });

        mOptions = (Category[]) getArguments().getParcelableArray(OPTIONS_EXTRA);
        if (mOptions != null) {
            mTitles = new String[mOptions.length];
            for (int i = 0; i < mOptions.length; i++) {
                mTitles[i] = mOptions[i].getName();
            }
            builder.setSingleChoiceItems(mTitles, mSelected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("Is selected: "+which);
                    mSelected = which;
                }
            });
        }

        return builder.create();
    }

    interface OnItemSelected {
        void onItemSelected(Category item, int position);
    }
}
