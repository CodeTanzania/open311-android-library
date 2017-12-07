package com.github.codetanzania.open311.android.library.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import com.auth0.android.jwt.JWT;
import com.github.codetanzania.open311.android.library.models.Party;
import com.github.codetanzania.open311.android.library.utils.SharedPrefsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Modifier;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Wrappers that allows to deal with authenticity and authorization of
 * parties
 *
 * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
 * @version 0.1.0
 * @since 0.1.0
 */

public final class Auth {
    /**
     * Valid shared preferences key used to set and get current party
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public static final String AUTH_PARTY = "AUTH_PARTY";

    /**
     * Exception message thrown when credential for signin are not valid
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public static final String EXCEPTION_INVALID_CREDENTIAL = "Invalid Credential";

    /**
     * Exception message thrown when no internet connection
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public static final String EXCEPTION_NO_NETWORK_CONNECTION = "No Network Connection";

    /**
     * Valid application context reference. It only set once during initialization
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    private Context context;

    /**
     * Valid instance of {@link Retrofit}
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    private Retrofit retrofit;

    /**
     * Valid instance of {@link Gson} converter
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    private Gson gson;

    /**
     * Valid base url of auth API endpoint
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    private String baseUrl;

    /**
     * Valid instance of {@link Party}. It only set once party validated and login.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    private Party party;

    /**
     * Valid {@link API} endpoint instance
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    private API api;

    /**
     * Valid instance of {@link Auth}. It only set once during initialization
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @SuppressLint("StaticFieldLeak")
    private static Auth instance;

    /**
     * Initialize {@link Auth} and return its instance
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public static synchronized Auth init(Context context, String baseUrl) {

        //ensure instance not exist
        if (instance == null) {

            //instantiate new auth
            instance = new Auth(context, baseUrl);

            //initialize auth
            instance.init();

        }

        return instance;

    }

    public static synchronized Auth getInstance() {
        return instance;
    }

    private Auth(Context context, String baseUrl) {
        this.context = context.getApplicationContext();
        this.baseUrl = baseUrl;
    }

    private void init() {

        //prepare gson converter
        this.gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create();

        //prepare auth api instance
        this.retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(this.gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.api = this.retrofit.create(API.class);

    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    //use for test only to inject mock api client
    public void setApi(API api) {
        this.api = api;
    }

    public Gson getGson() {
        return gson;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Check if there is internet or data connection on the device
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public Boolean isConnected() {
        ConnectivityManager connectivity =
                (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Set {@link Party} in memory and persist into {@link android.content.SharedPreferences}
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public synchronized void setParty(Party party) {

        if (party != null) {
            //set in memory
            this.party = party;

            //save in preferences
            String partyJson = this.gson.toJson(this.party);
            SharedPrefsUtils.set(this.context, AUTH_PARTY, partyJson);
        }

    }

    /**
     * Get {@link Party} from memory and if not exists fetch it from {@link android.content.SharedPreferences}
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public synchronized Party getParty() {

        if (this.party == null) {
            //fetch from preferences
            String partyJson = SharedPrefsUtils.get(this.context, AUTH_PARTY, "");

            //convert party json to instance
            if (partyJson != null && !partyJson.isEmpty()) {
                try {
                    Party party;
                    party = this.gson.fromJson(partyJson, Party.class);
                    this.party = party;
                } catch (Exception e) {
                    //do nothing
                }
            }
        }

        return this.party;
    }


    /**
     * Signin party using provided credentials
     *
     * @param email
     * @param password
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public synchronized Observable<Party> signin(String email, String password) {
        //TODO if token exists and not expired return existing party

        //initialize credential
        Credential credential = new Credential(email, password);

        Observable<Party> party;

        party = Observable
                .just(credential)
                .map(new Function<Credential, Credential>() { // validate credential
                    @Override
                    public Credential apply(Credential credential) throws Exception {

                        //validate credential
                        Boolean isValidCredential = credential.isValid();
                        if (!isValidCredential) {
                            throw new Exception(EXCEPTION_INVALID_CREDENTIAL);
                        }

                        return credential;
                    }
                })
                .map(new Function<Credential, Observable<Response>>() {//api signin
                    @Override
                    public Observable<Response> apply(Credential credential) throws Exception {
                        return api.signin(credential);
                    }
                })
                .map(new Function<Observable<Response>, Party>() { //obtain party
                    @Override
                    public Party apply(Observable<Response> response) throws Exception {
                        Response authResponse = response.blockingFirst();
                        if (authResponse.isSuccess()) {
                            //obtain signin party
                            Party party = authResponse.getParty();
                            party.setToken(authResponse.getToken());

                            //set party
                            setParty(party);

                            return party;
                        } else {
                            throw new Exception(authResponse.getMessage());
                        }
                    }
                });

        return party;
    }

    /**
     * Provide authorization token of current logged-in {@link Party}. Token is not checked
     * whether is already expired.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public synchronized String getToken() {

        //ensure party available
        this.getParty();
        if (this.party != null) {
            //obtain token from logged in party
            String token;
            token = this.party.getToken();
            return token;
        }

        //always return no party exists
        return null;

    }

    /**
     * Verify whether authorization token of current logged-in {@link Party} is expired.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public synchronized Boolean isTokenExpired() {

        //obtain token
        String token = this.getToken();

        if (token != null && !token.isEmpty()) {
            try {
                //jwt decode token
                JWT jwt = new JWT(token);

                //verify token is not expired
                boolean isExpired;
                isExpired = jwt.isExpired(0);
                return isExpired;

            } catch (Exception e) {
                //do nothing
            }
        }

        //always return expired
        return true;

    }

    /**
     * Verify whether there is a current logged-in {@link Party} and
     * authorization token is not expired.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    public synchronized Boolean isLogin() {
        //check for token expiry
        Boolean isTokenExpired;
        isTokenExpired = this.isTokenExpired();
        return !isTokenExpired;
    }

    /**
     * {@link Auth} Credential Payload.
     *
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @version 0.1.0
     * @since 0.1.0
     */
    public static final class Credential {
        @Expose
        @SerializedName("email")
        private String email;

        @Expose
        @SerializedName("password")
        private String password;

        public Credential() {
        }

        public Credential(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean isValid() {
            //ensure valid email
            Boolean isValidEmail =
                    (this.email != null && !this.email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());

            //ensure valid password
            Boolean isValidPassword = (this.password != null && !this.password.isEmpty());

            return isValidEmail && isValidPassword;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Credential that = (Credential) o;

            if (email != null ? !email.equals(that.email) : that.email != null) return false;
            return password != null ? password.equals(that.password) : that.password == null;
        }

        @Override
        public int hashCode() {
            int result = email != null ? email.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return email;
        }
    }

    /**
     * {@link Auth} API endpoint response.
     *
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @version 0.1.0
     * @since 0.1.0
     */
    public static class Response {
        @Expose
        @SerializedName("success")
        private Boolean success;

        @Expose
        @SerializedName("party")
        private Party party;

        @Expose
        @SerializedName("token")
        private String token;

        @Expose
        @SerializedName("message")
        private String message;

        public Response() {
        }

        public Boolean getSuccess() {
            return success;
        }

        public Boolean isSuccess() {
            return getSuccess();
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public Party getParty() {
            return party;
        }

        public void setParty(Party party) {
            this.party = party;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * {@link Auth} API endpoints.
     *
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @version 0.1.0
     * @since 0.1.0
     */
    public interface API {
        @POST("/signin")
        @Headers({
                "Accept: application/json",
                "Content-Type: application/json"
        })
        Observable<Response> signin(@Body Credential credential);
    }
}
