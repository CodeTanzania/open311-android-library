package com.example.majifix311.shadows;

import android.app.Activity;
import android.net.Uri;

import com.example.majifix311.utils.AttachmentUtils;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * This is used to mock a camera intent.
 */

@Implements(AttachmentUtils.class)
public class ShadowAttachmentUtils {
}
