package com.example.majifix311.ui;

import android.app.Activity;
import android.content.Context;
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

import com.example.majifix311.R;

import java.util.List;

/**
 * This is used to pick between categories/services.
 */

public class CategoryPickerDialog extends AlertDialog {
    private List<String> mCategories;
    private onItemSelected mListener;

    private ListView mListView;
    private Button mBtnSelect;
    private Button mBtnCancel;

    private int mSelected;

    protected CategoryPickerDialog(@NonNull Context context, List<String> categories) {
        super(context);
        mCategories = categories;
        prepView();
    }

    protected CategoryPickerDialog(@NonNull Context context, List<String> categories,
                                   @StyleRes int themeResId) {
        super(context, themeResId);
        mCategories = categories;
        prepView();
    }

    protected CategoryPickerDialog(@NonNull Context context, List<String> categories,
                                   boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mCategories = categories;
        prepView();
    }

    public void setSelected(String category) {
        mSelected = mCategories.indexOf(category);
    }

    public void setListener(onItemSelected listener) {
        mListener = listener;
    }

    private void prepView() {
        Activity activity = getOwnerActivity();
        if (activity == null) {
            return;
        }
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_category_selector, null);
        mListView = (ListView) view.findViewById(R.id.dialogList);
        mListView.setAdapter(getArrayAdapter());

        mBtnSelect = (Button) view.findViewById(R.id.select);
        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemSelected(mCategories.get(mSelected), mSelected);
                }
                dismiss();
            }
        });

        mBtnCancel = (Button) view.findViewById(R.id.close);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private ArrayAdapter<String> getArrayAdapter() {
        return new ArrayAdapter<String>(getContext(), R.layout.item_category_selector, mCategories) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                Activity activity = getOwnerActivity();
                if (activity != null) {
                    View view = getOwnerActivity().getLayoutInflater().inflate(
                            R.layout.item_category_selector, null);

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    checkBox.setChecked(position == mSelected);
                    checkBox.setTag(position);
                    checkBox.setText(mCategories.get(position));
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelected = (int) v.getTag();
                            notifyDataSetChanged();
                        }
                    });

                    return view;
                }
                return null;
            }
        };
    }

    interface onItemSelected {
        void onItemSelected(String item, int position);
    }
}
