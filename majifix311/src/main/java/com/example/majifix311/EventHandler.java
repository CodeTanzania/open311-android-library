package com.example.majifix311;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.majifix311.models.Problem;

/**
 * This is used for sending updates throughout the application.
 */

public class EventHandler {
    public final static String BROADCAST_REPORT_RECIEVED = "com.majifix311.broadcast.REPORT_RECEIVED";
    public final static String IS_SUCCESS = "is success";
    public final static String PROBLEM_INTENT = "problem";
    public final static String ERROR_INTENT = "error";

    public static void sendReportReceived(Context context, Problem problem) {
        Intent resultIntent = new Intent();
        resultIntent.setAction(BROADCAST_REPORT_RECIEVED);
        resultIntent.putExtra(IS_SUCCESS, true);
        resultIntent.putExtra(PROBLEM_INTENT, problem);
        LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
    }

    public static void sendReportError(Context context, Throwable error) {
        Intent resultIntent = new Intent();
        resultIntent.setAction(BROADCAST_REPORT_RECIEVED);
        resultIntent.putExtra(IS_SUCCESS, false);
        resultIntent.putExtra(ERROR_INTENT, error);
        LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
    }

}
