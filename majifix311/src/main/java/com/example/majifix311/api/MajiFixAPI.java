package com.example.majifix311.api;

import com.example.majifix311.BuildConfig;
import com.example.majifix311.Problem;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
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
    private Retrofit mRetrofit;
    private MajiFixRetrofitApi mApi;

    MajiFixAPI(String baseUrl) {
        initRetrofit(baseUrl);
    }

    private void initRetrofit(String baseUrl) {
        // Configures retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // Provides majifix endpoints
        mApi = mRetrofit.create(MajiFixRetrofitApi.class);
    }

    void reportProblem(Problem problem, Consumer<Problem> onNext, Consumer<Throwable> onError) {
        System.out.println("report problem started");
        // Convert problem into the format needed by the server
        ApiServiceRequestPost request = ApiModelConverter.convert(problem);
        // Define call
        Observable<ApiServiceRequestGet> response = mApi.postProblem(getAuthToken(), request);
        // Schedule the call
        response.subscribeOn(Schedulers.io())
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
        @POST("/servicerequests")
        @Headers({"Content-Type: application/json"})
        Observable<ApiServiceRequestGet> postProblem(
                @Header("Authorization") String authorization,
                @Body ApiServiceRequestPost newProblem);

    }
}
