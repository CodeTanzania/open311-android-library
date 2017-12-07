package com.github.codetanzania.open311.android.library.ui.report;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

import com.github.codetanzania.open311.android.library.ui.R;

import static android.text.TextUtils.isEmpty;

/**
 * This is used to observe text changes, and inform the UI if new text is empty.
 */

public class EmptyErrorTrigger implements TextWatcher {
    private TextInputLayout mErrorLayout;

    public EmptyErrorTrigger(TextInputLayout textInputLayout) {
        mErrorLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Check if text is empty, and if so, trigger callback
        if (isEmpty(s) || isEmpty(s.toString().trim())) {
            mErrorLayout.setError(mErrorLayout.getResources().getString(R.string.required));
        } else {
            mErrorLayout.setError(null);
        }
    }
}
