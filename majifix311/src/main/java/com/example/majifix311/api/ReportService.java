package com.example.majifix311.api;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.example.majifix311.EventHandler;
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.models.Problem;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                    fetchProblemsChooser(intent.getStringExtra(FETCH_REQUESTS_INTENT));
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

    private void fetchProblemsChooser(String number) {
        final DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if (!(e instanceof CancellationException)) {
                    EventHandler.errorRetrievingRequests(getApplicationContext(), e);
                }
            }
        };

        final Observable<ArrayList<Problem>> fakeDB = new Observable<ArrayList<Problem>>() {
            @Override
            protected void subscribeActual(Observer<? super ArrayList<Problem>> observer) {
                Log.d(TAG, "Fake DB Observer ran on " + Thread.currentThread());
                observer.onNext(new ArrayList<Problem>());
            }
        }
                .subscribeOn(Schedulers.io())
                .delay(10, TimeUnit.SECONDS);

        final Observable<ArrayList<Problem>> realDB =
                db.retrieveMyReportedProblems().subscribeOn(Schedulers.io()).toObservable();


        Function<
                Observable<Pair<ArrayList<Problem>, Boolean>>,
                ObservableSource<Pair<ArrayList<Problem>, Boolean>>
                > merger =
                new Function<
                        Observable<Pair<ArrayList<Problem>, Boolean>>,
                        ObservableSource<Pair<ArrayList<Problem>, Boolean>>
                        >() {
                    @Override
                    public ObservableSource<Pair<ArrayList<Problem>, Boolean>> apply(
                            Observable<Pair<ArrayList<Problem>, Boolean>> network) throws Exception {
                        return Observable.mergeDelayError(
                                network,
                                realDB.takeUntil(network).map(preliminizer(true))
                        );
                    }
                };

        majiFixAPI.getProblemsByPhoneNumber(number)
                .subscribeOn(Schedulers.io())
                .flatMapObservable(new Function<ArrayList<Problem>, ObservableSource<ArrayList<Problem>>>() {
                    @Override
                    public ObservableSource<ArrayList<Problem>> apply(ArrayList<Problem> problems) throws Exception {
                        return db.saveMyReportedProblems(problems).toObservable();
                    }
                })
                .map(preliminizer(false))
                .publish(merger)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Pair<ArrayList<Problem>,Boolean>>() {
                            @Override
                            public void accept(Pair<ArrayList<Problem>,Boolean> problems) throws Exception {
                                EventHandler.retrievedMyRequests(
                                        getApplicationContext(),
                                        problems.first,
                                        problems.second
                                );
                            }
                        },
                        errorConsumer
                );
    }

    private Function<ArrayList<Problem>, Pair<ArrayList<Problem>, Boolean>> preliminizer(final boolean isPreliminary) {
        return new Function<ArrayList<Problem>, Pair<ArrayList<Problem>, Boolean>>() {
            @Override
            public Pair<ArrayList<Problem>, Boolean> apply(ArrayList<Problem> problems) throws Exception {
                return new Pair<>(problems, isPreliminary);
            }
        };
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
