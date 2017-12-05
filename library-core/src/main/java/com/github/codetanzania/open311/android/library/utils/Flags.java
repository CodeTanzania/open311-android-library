package com.github.codetanzania.open311.android.library.utils;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * This is used for Enum types.
 */

public class Flags {
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
