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
import com.example.majifix311.db.DatabaseHelper;
import com.example.majifix311.models.Problem;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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

        final Observable<ArrayList<Problem>> realDB = db.retrieveMyReportedProblems().toObservable();

        Function<Observable<ArrayList<Problem>>, ObservableSource<ArrayList<Problem>>> merger =
                new Function<Observable<ArrayList<Problem>>, ObservableSource<ArrayList<Problem>>>() {
                    @Override
                    public ObservableSource<ArrayList<Problem>> apply(Observable<ArrayList<Problem>> observable) throws Exception {
                        return Observable.mergeDelayError(observable, realDB.takeUntil(observable));
                    }
                };

        majiFixAPI.getProblemsByPhoneNumber(number)
                .subscribeOn(Schedulers.io())
                //.flatMapObservable(new Function<ArrayList<Problem>, ObservableSource<ArrayList<Problem>>>() {
                //    @Override
                //    public ObservableSource<ArrayList<Problem>> apply(ArrayList<Problem> problems) throws Exception {
                //        return db.saveMyReportedProblems(problems).toObservable();
                //    }
                //})
                .toObservable()
                .publish(merger)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<ArrayList<Problem>>() {
                            @Override
                            public void accept(ArrayList<Problem> problems) throws Exception {
                                EventHandler.retrievedMyRequests(getApplicationContext(), problems);
                            }
                        },
                        errorConsumer
                );


/*        final SingleObserver<ArrayList<Problem>> observer = new SingleObserver<ArrayList<Problem>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(ArrayList<Problem> problems) {
                //db.saveMyReportedProblems();
                EventHandler.retrievedMyRequests(getApplicationContext(), problems);
            }

            @Override
            public void onError(Throwable e) {
                EventHandler.errorRetrievingRequests(getApplicationContext(), e);
            }
        };

        db.retrieveMyReportedProblems()
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Problem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ArrayList<Problem> problems) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {}
                })
                new Consumer<ArrayList<Problem>>() {
                    @Override
                    public void accept(ArrayList<Problem> problems) throws Exception {
                        EventHandler.retrievedMyRequests(getApplicationContext(), problems);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        observer.onError(throwable);
                    }
                },
        true);

        majiFixAPI.getProblemsByPhoneNumber(observer, number);*/
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
