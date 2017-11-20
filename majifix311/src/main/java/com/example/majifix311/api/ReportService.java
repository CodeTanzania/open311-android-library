package com.example.majifix311.api;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.majifix311.EventHandler;
import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestGetMany;
import com.example.majifix311.models.Problem;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * This service submits problems to a municipal company that uses the majifix system.
 */


public class ReportService extends Service {
    private static final String TAG = "ReportService";
    public final static String NEW_PROBLEM_INTENT = "problem";
    public static final String FETCH_REQUESTS_INTENT = "fetch";
    public final static String START_POST_PROBLEM_ACTION =
            "com.example.majifix311.api.action.startpostproblem";
    public static final String START_FETCH_PROBLEMS_ACTION =
            "com.example.majifix311.api.action.startfetchproblems";
    public final static String STOPFOREGROUND_ACTION = "com.example.majifix311.api.action.stopforeground";

    private final ProblemReportBinder mBinder = new ProblemReportBinder();
    private MajiFixAPI majiFixAPI;

    public static void postNewProblem(Context context, Problem problem) {
        Intent startIntent = new Intent(context, ReportService.class);
        startIntent.setAction(ReportService.START_POST_PROBLEM_ACTION);
        startIntent.putExtra(ReportService.NEW_PROBLEM_INTENT, problem);
        context.startService(startIntent);
    }

    public static void fetchProblems(Context context, String number) {
        Intent startIntent = new Intent(context, ReportService.class);
        startIntent.setAction(ReportService.START_FETCH_PROBLEMS_ACTION);
        startIntent.putExtra(ReportService.FETCH_REQUESTS_INTENT, number);
        context.startService(startIntent);
    }

    @Override
    public void onCreate() {
        // Called once on initial create of service
        majiFixAPI = MajiFixAPI.getInstance();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case START_FETCH_PROBLEMS_ACTION:
                    String number = intent.getStringExtra(FETCH_REQUESTS_INTENT);
                    majiFixAPI.getProblemsByPhoneNumber(new SingleObserver<ArrayList<Problem>>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onSuccess(ArrayList<Problem> problems) {
                            EventHandler.retrievedMyRequests(getApplicationContext(),problems);
                        }

                        @Override
                        public void onError(Throwable e) {
                            EventHandler.errorRetrievingRequests(getApplicationContext(),e);
                        }
                    }, number);
                    break;
                case START_POST_PROBLEM_ACTION:
                    Problem problem = intent.getParcelableExtra(NEW_PROBLEM_INTENT);
                    majiFixAPI.reportProblem(problem, onNext(), onError());
                    break;
            }
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
                System.out.println("onRetrievedFromNetwork! " + problem);
                EventHandler.sendReportReceived(getApplicationContext(), problem);
            }
        };
    }

    protected Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable error) throws Exception {
                System.out.println("onError! " + error);
                EventHandler.sendReportError(getApplicationContext(), error);
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
