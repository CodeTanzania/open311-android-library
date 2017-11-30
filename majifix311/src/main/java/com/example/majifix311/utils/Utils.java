package com.example.majifix311.utils;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * This is used for Utility methods.
 */

public class Utils {

    // This can be used to save the state of a ui (see ProblemListActivity for an example)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NONE, EMPTY, LOADING, SUCCESS, ERROR})
    public @interface UiState {}
    public static final String NONE = "none";
    public static final String EMPTY = "empty";
    public static final String LOADING = "loading";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
}
