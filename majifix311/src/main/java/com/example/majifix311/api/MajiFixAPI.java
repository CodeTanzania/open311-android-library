package com.example.majifix311.api;

import com.example.majifix311.MajiFix;
import com.example.majifix311.Problem;
import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.api.models.ApiServiceGroup;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

/**
 * This provides the endpoints for the MajiFix API.
 *
 * Thanks to the following resources:
 *   https://medium.com/3xplore/handling-api-calls-using-retrofit-2-and-rxjava-2-1871c891b6ae
 *   http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/
 */

class MajiFixAPI {
    private static MajiFixAPI mSingleton;

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
        Observable<ApiServiceGroup> call = mApi.getCategories(getAuthToken());
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    void reportProblem(Problem problem, Consumer<Problem> onNext, Consumer<Throwable> onError) {
        System.out.println("report problem started");
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

    private String getAuthToken() {
        //TODO obfuscate token
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU5YjFlM2U0ZTkxNDFkMDAwNDJjMjMwNiIsImlhdCI6MTUwNDgzMDQzNiwiZXhwIjozMzA2MjQzMDQzNiwiYXVkIjoib3BlbjMxMSJ9.WhkA4bI5O0UJHvoYtIwa9o-LdDu51rr9TuTa0r0nG2A";
    }

    private interface MajiFixRetrofitApi {
        @GET("/services")
        @Headers({"Accept: application/json", "Content-Type: application/json"})
        Observable<ApiServiceGroup> getCategories(
                @Header("Authorization") String authHeader);

        @POST("/servicerequests")
        @Headers({"Content-Type: application/json"})
        Observable<ApiServiceRequestGet> postProblem(
                @Header("Authorization") String authorization,
                @Body ApiServiceRequestPost newProblem);

    }
}
