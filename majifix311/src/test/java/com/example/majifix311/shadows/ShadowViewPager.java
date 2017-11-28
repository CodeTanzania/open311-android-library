package com.example.majifix311.shadows;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import org.robolectric.Robolectric;
import org.robolectric.ShadowsAdapter;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowViewGroup;

import static org.robolectric.shadow.api.Shadow.directlyOn;

@Implements(ViewPager.class)
public class ShadowViewPager extends ShadowViewGroup {
    @RealObject ViewPager realViewPager;

    @Implementation
    public void setAdapter(final PagerAdapter adapter) {
        ShadowsAdapter shadowsAdapter = Robolectric.getShadowsAdapter();
        shadowsAdapter
                .getMainLooper()
                .runPaused(new Runnable() {
                    @Override
                    public void run() {
                        directlyOn(realViewPager, ViewPager.class).setAdapter(adapter);
                    }
                });
    }

    @Implementation
    public void setOffscreenPageLimit(final int limit) {
        ShadowsAdapter shadowsAdapter = Robolectric.getShadowsAdapter();
        shadowsAdapter
                .getMainLooper()
                .runPaused(new Runnable() {
                    @Override
                    public void run() {
                        directlyOn(realViewPager, ViewPager.class).setOffscreenPageLimit(limit);
                    }
                });
    }

    @Implementation
    public void setCurrentItem(int item) {
        ShadowLooper.pauseMainLooper();
        System.out.println("Set item: "+item);
        realViewPager.setCurrentItem(item, true);
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
    }
}
