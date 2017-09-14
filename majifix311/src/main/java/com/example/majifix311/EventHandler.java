package com.example.majifix311;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * This is used for sending updates throughout the application.
 */

public class EventHandler {
    public final static String BROADCAST_REPORT_RECIEVED = "com.majifix311.broadcast.REPORT_RECEIVED";

    public static void sendResultReceived(Context context) {
        Intent resultIntent = new Intent();
        resultIntent.setAction(BROADCAST_REPORT_RECIEVED);
        resultIntent.putExtra("data","All is well in the world!");
        LocalBroadcastManager.getInstance(context).sendBroadcast(resultIntent);
    }

}
