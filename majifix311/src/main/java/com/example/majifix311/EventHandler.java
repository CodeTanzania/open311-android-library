package com.example.majifix311;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.majifix311.models.Problem;

import java.util.ArrayList;

/**
 * This is used for sending updates throughout the application.
 */

public class EventHandler {
    public final static String BROADCAST_REPORT_RECIEVED =
            "com.majifix311.broadcast.REPORT_RECEIVED";
    public final static String BROADCAST_MY_REPORTED_RECIEVED =
            "com.majifix311.broadcast.MY_REPORTED_RECEIVED";
    public static final String BROADCAST_MY_PROBLEMS_FETCHED =
            "com.majifix311.broadcast.MY_PROBLEMS_FETCHED";
    public final static String IS_SUCCESS = "is success";
    public static final String IS_PRELIMINARY_DATA = "is preliminary";
    public final static String PROBLEM_INTENT = "problem";
    public final static String ERROR_INTENT = "error";
    public static final String REQUEST_LIST = "requests";

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

    //public static void sendMyReportedProblemsList(
    //        Context context, ArrayList<Problem> problemList) {
    //    System.out.println("Sending list of problems: "+problemList.size());
    //
    //    Intent resultIntent = new Intent();
    //    resultIntent.setAction(BROADCAST_MY_REPORTED_RECIEVED);
    //    resultIntent.putExtra(IS_SUCCESS, true);
    //    resultIntent.putExtra(PROBLEM_INTENT, problemList);
    //    LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
    //}

    public static void retrievedMyRequests(Context context, ArrayList<Problem> problems, boolean isPreliminary){
        Intent resultIntent = new Intent();
        resultIntent.setAction(BROADCAST_MY_PROBLEMS_FETCHED);
        resultIntent.putExtra(IS_SUCCESS, true);
        resultIntent.putExtra(IS_PRELIMINARY_DATA, isPreliminary);
        resultIntent.putExtra(REQUEST_LIST, problems);
        LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
    }

    public static void errorRetrievingRequests(Context context, Throwable error){
        System.out.println("Erred on request from server");

        Intent resultIntent = new Intent();
        resultIntent.setAction(BROADCAST_MY_PROBLEMS_FETCHED);
        resultIntent.putExtra(IS_SUCCESS, false);
        resultIntent.putExtra(ERROR_INTENT, error);
        LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
    }

}
