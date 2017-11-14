package com.example.majifix311.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.majifix311.R;
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.models.Category;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

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
