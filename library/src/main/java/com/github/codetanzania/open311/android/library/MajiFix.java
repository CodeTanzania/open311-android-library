package com.github.codetanzania.open311.android.library;

import android.content.Context;

import com.github.codetanzania.open311.android.library.api.CategoriesManager;
import com.github.codetanzania.open311.android.library.auth.Auth;
import com.github.codetanzania.open311.android.library.utils.AttachmentUtils;

/**
 * This is used as the base library file, and is used for configuration by applications.
 *
 * It is inspired by the old way that glide was incorporated into a project.
 * See: https://github.com/bumptech/glide/wiki/Configuration/b3641efda7d78ec75019f0e4b387f977a7364d37#in-your-application
 */

public class MajiFix {
    private static boolean isSetup;
    private static String mEndpoint = BuildConfig.END_POINT;

    public static void setup(Context context) {
        isSetup = true;

        // Initialize auth for token management. DO BEFORE NETWORK CALLS!
        Auth.init(context, BuildConfig.END_POINT);

        //TODO take config from builder for app

        //TODO does this go here?
        // Get categories and save in db
        CategoriesManager service = new CategoriesManager(context);
        service.getCategories();

        // Set directory for saving temporary attachment files
        AttachmentUtils.setCacheDirectory(context.getCacheDir());
    }

    public boolean isSetup() {
        return isSetup;
    }

    public static void setBaseEndpoint(String endpoint) {
        mEndpoint = endpoint;
    }

    public static String getBaseEndpoint() {
        return mEndpoint;
    }
}
