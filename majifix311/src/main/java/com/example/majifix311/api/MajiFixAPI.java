package com.example.majifix311.api;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.example.majifix311.BuildConfig;
import com.example.majifix311.MajiFix;
import com.example.majifix311.api.models.ApiServiceRequestGetMany;
import com.example.majifix311.models.Problem;
import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * This provides the endpoints for the MajiFix API.
 * <p>
 * Thanks to the following resources:
 * https://medium.com/3xplore/handling-api-calls-using-retrofit-2-and-rxjava-2-1871c891b6ae
 * http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/
 */

@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
public class MajiFixAPI {
    private static MajiFixAPI mSingleton;

    private static final String TAG = "MajiFixAPI";
    private Retrofit mRetrofit;
    private MajiFixRetrofitApi mApi;

    private MajiFixAPI() {
        initRetrofit();
    }

    public static MajiFixAPI getInstance() {
        if (mSingleton == null) {
            mSingleton = new MajiFixAPI();
        }
        return mSingleton;
    }

    private void initRetrofit() {
        // Configures retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(MajiFix.getBaseEndpoint())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // Provides majifix endpoints
        mApi = mRetrofit.create(MajiFixRetrofitApi.class);
    }

    void getServices(Consumer<ApiServiceGroup> onNext, Consumer<Throwable> onError) {
        //  limit the response to return publicly displayed service categories
        Observable<ApiServiceGroup> call = mApi.getCategories(getAuthToken(), "{\"isExternal\":\"true\"}");
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    void reportProblem(Problem problem, Consumer<Problem> onNext, Consumer<Throwable> onError) {
        System.out.println("report problem started");
        // TODO convert in thread??
        // Convert problem into the format needed by the server
        ApiServiceRequestPost request = ApiModelConverter.convert(problem);
        // Define call
        Observable<ApiServiceRequestGet> call = mApi.postProblem(getAuthToken(), request);
        // Schedule the call
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ApiServiceRequestGet, Problem>() {
                    @Override
                    public Problem apply(ApiServiceRequestGet apiServiceRequest) throws Exception {
                        // convert the server object into something the app can use
                        System.out.println("Conversion taking place! " + apiServiceRequest);
                        return ApiModelConverter.convert(apiServiceRequest);
                    }
                })
                .subscribe(onNext, onError);
    }

    public Single<ArrayList<Problem>> getProblemsByPhoneNumber(final String phoneNumber) {
        final Single<ApiServiceRequestGetMany> initCall = generateCall(phoneNumber, 1).cache();

        return initCall
                .flatMapObservable(new Function<ApiServiceRequestGetMany, ObservableSource<ApiServiceRequestGetMany>>() {
                    @Override
                    public ObservableSource<ApiServiceRequestGetMany> apply(ApiServiceRequestGetMany feed) throws Exception {
                        int numPages = feed.getPages();
                        List<Single<ApiServiceRequestGetMany>> pages = new ArrayList<>(numPages);
                        pages.add(0, initCall);
                        for (int i = 1; i < numPages; i++) {
                            pages.add(i, generateCall(phoneNumber, i + 1));
                        }
                        return Single.concat(pages).toObservable();
                    }
                })
                .concatMap(new Function<ApiServiceRequestGetMany, Observable<ApiServiceRequestGet>>() {
                    @Override
                    public Observable<ApiServiceRequestGet> apply(ApiServiceRequestGetMany getMany) throws Exception {
                        return Observable.fromIterable(getMany.getServicerequests());
                    }
                })
                .map(new Function<ApiServiceRequestGet, Problem>() {
                    @Override
                    public Problem apply(ApiServiceRequestGet apiServiceRequest) throws Exception {
                        return ApiModelConverter.convert(apiServiceRequest);
                    }
                })
                .collectInto(new ArrayList<Problem>(), new BiConsumer<List<Problem>, Problem>() {
                    @Override
                    public void accept(List<Problem> list, Problem problem) throws Exception {
                        list.add(problem);
                    }
                });
    }

    private Single<ApiServiceRequestGetMany> generateCall(String phoneNumber, int page) {
        return mApi.getReportsWPhoneNo(
                getAuthToken(),
                "{\"reporter.phone\":\"" + phoneNumber + "\"}",
                page
        );
    }

    private String getAuthToken() {
        //TODO obfuscate token
        String authToken = "Bearer " + BuildConfig.MAJIFIX_API_TOKEN;
        System.out.println("Auth token: " + authToken);
        return authToken;
    }

    private interface MajiFixRetrofitApi {
        @GET("/services")
        @Headers({"Accept: application/json", "Content-Type: application/json"})
        Observable<ApiServiceGroup> getCategories(
                @Header("Authorization") String authHeader,
                @Query("query") String query);

        //TODO get GSON to directly spit out ApiServiceRequestGet objects.
        // Use CustomTypeFactory? https://stackoverflow.com/a/43459059
        @GET("/servicerequests")
        @Headers({"Accept: application/json", "Content-Type: application/json"})
        Single<ApiServiceRequestGetMany> getReportsWPhoneNo(
                @Header("Authorization") String authorization,
                @Query("query") String query,
                @Query("page") int page
        );

        @POST("/servicerequests")
        @Headers({"Content-Type: application/json"})
        Observable<ApiServiceRequestGet> postProblem(
                @Header("Authorization") String authorization,
                @Body ApiServiceRequestPost newProblem);
    }
}
