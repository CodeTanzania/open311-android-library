package com.example.majifix311.api;

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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

/**
 * This provides the endpoints for the MajiFix API.
 *
 * Thanks to the following resources:
 *   https://medium.com/3xplore/handling-api-calls-using-retrofit-2-and-rxjava-2-1871c891b6ae
 *   http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/
 */

class MajiFixAPI {
    private static MajiFixAPI mSingleton;

    private static final String TAG = "MajiFixAPI";
    private Retrofit mRetrofit;
    private MajiFixRetrofitApi mApi;
    private Observable<ApiServiceGroup> mRequestCache;

    private MajiFixAPI() {
        initRetrofit();
    }

    static MajiFixAPI getInstance() {
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
                        System.out.println("Conversion taking place! "+apiServiceRequest);
                        return ApiModelConverter.convert(apiServiceRequest);
                    }
                })
                .subscribe(onNext, onError);
    }

    void getProblemsByPhoneNumber(SingleObserver<ArrayList<Problem>> observer, String phoneNumber){

        String test = "{\"reporter.phone\":\"" + phoneNumber + "\"}";
        Log.d(TAG, "getProblemsByPhoneNumber: " + test);
        Single<ApiServiceRequestGetMany> call = mApi.getReportsWPhoneNo(getAuthToken(),test
                );

        ArrayList<Problem> problems = new ArrayList<>();

        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapObservable(new Function<ApiServiceRequestGetMany,Observable<ApiServiceRequestGet>>(){
                    @Override
                    public Observable<ApiServiceRequestGet> apply(ApiServiceRequestGetMany getMany) throws Exception {
                        return Observable.fromIterable(getMany.getServicerequests());
                    }
                })
                .map(new Function<ApiServiceRequestGet, Problem>() {
                    @Override
                    public Problem apply(ApiServiceRequestGet apiServiceRequest) throws Exception {
                        // convert the server object into something the app can use
                        System.out.println("Conversion taking place! "+apiServiceRequest);
                        return ApiModelConverter.convert(apiServiceRequest);
                    }
                })
                .collectInto(problems, new BiConsumer<List<Problem>, Problem>() {
                    @Override
                    public void accept(List<Problem> list, Problem problem) throws Exception {
                        list.add(problem);
                    }
                })
                .subscribe(observer);
    }

    private String getAuthToken() {
        //TODO obfuscate token
        String authToken = "Bearer "+ BuildConfig.APP_TOKEN;
        System.out.println("Auth token: "+authToken);
        return "Bearer "+ BuildConfig.APP_TOKEN;
    }

    private interface MajiFixRetrofitApi {
        @GET("/services")
        @Headers({"Accept: application/json", "Content-Type: application/json"})
        Observable<ApiServiceGroup> getCategories(
                @Header("Authorization") String authHeader,
                @Query("query") String query);


        //TODO this is probably an invalid return generic type
        @GET("/servicerequests")
        @Headers({"Accept: application/json", "Content-Type: application/json"})
        Single<ApiServiceRequestGetMany> getReportsWPhoneNo(
            @Header("Authorization") String authorization,
            @Query("query") String query
        );

        @POST("/servicerequests")
        @Headers({"Content-Type: application/json"})
        Observable<ApiServiceRequestGet> postProblem(
                @Header("Authorization") String authorization,
                @Body ApiServiceRequestPost newProblem);



    }
}
