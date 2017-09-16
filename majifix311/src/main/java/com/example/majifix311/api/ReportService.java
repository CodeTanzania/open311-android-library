package com.example.majifix311.api;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.majifix311.EventHandler;
import com.example.majifix311.Problem;

import io.reactivex.functions.Consumer;


/**
 * This service submits problems to a municipal company that uses the majifix system.
 */

public class ReportService extends Service {
    public final static String NEW_PROBLEM_INTENT = "problem";
    public final static String STARTFOREGROUND_ACTION = "com.example.majifix311.api.action.startforeground";
    public final static String STOPFOREGROUND_ACTION = "com.example.majifix311.api.action.stopforeground";

    private final ProblemReportBinder mBinder = new ProblemReportBinder();
    private MajiFixAPI majiFixAPI;
//    private Observable<String> mObservable;

    @Override
    public void onCreate() {
        // Called once on initial create of service
        majiFixAPI = new MajiFixAPI();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            Problem problem = intent.getParcelableExtra(NEW_PROBLEM_INTENT);
            majiFixAPI.reportProblem(problem, onNext(), onError());
        }
        // If the system kills the service after onStartCommand() returns, recreate the service
        // and call onStartCommand() with the last intent that was delivered to the service.
        // Any pending intents are delivered in turn.
        return START_REDELIVER_INTENT;
    }

    protected Consumer<Problem> onNext() {
        return new Consumer<Problem>() {
            @Override
            public void accept(Problem problem) throws Exception {
                System.out.println("onNext! "+problem);
                EventHandler.sendResultReceived(getApplicationContext());
            }
        };
    }

    protected Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable error) throws Exception {
                System.out.println("onError! "+error);
                EventHandler.sendResultReceived(getApplicationContext());
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class ProblemReportBinder extends Binder {
        ReportService getService() {
            return ReportService.this;
        }
    }
}
