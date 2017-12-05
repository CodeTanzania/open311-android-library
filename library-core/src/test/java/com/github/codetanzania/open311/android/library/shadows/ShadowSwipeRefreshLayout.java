package com.github.codetanzania.open311.android.library.shadows;

import android.support.v4.widget.SwipeRefreshLayout;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowViewGroup;

/**
 * This is used to programmatically call onRefresh in tests.
 * Thanks to: https://github.com/hidroh/materialistic/blob/master/app/src/test/java/io/github/hidroh/materialistic/test/shadow/ShadowSwipeRefreshLayout.java
 */

@Implements(value = SwipeRefreshLayout.class, inheritImplementationMethods = true)
public class ShadowSwipeRefreshLayout extends ShadowViewGroup {
    private SwipeRefreshLayout.OnRefreshListener mListener;

    @Implementation
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mListener = listener;
    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return mListener;
    }
}
