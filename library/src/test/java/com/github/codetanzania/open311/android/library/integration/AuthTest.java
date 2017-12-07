package com.github.codetanzania.open311.android.library.integration;

import android.content.Context;

import com.github.codetanzania.open311.android.library.BuildConfig;
import com.github.codetanzania.open311.android.library.auth.Auth;
import com.github.codetanzania.open311.android.library.auth.Auth.Response;
import com.github.codetanzania.open311.android.library.models.Party;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * {@link Auth} Tests
 *
 * @author lally elias
 */

@Config(sdk = 23, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class AuthTest {

    private Context context;
    private final NetworkBehavior behavior = NetworkBehavior.create();
    private final TestObserver<Response> testObserver = TestObserver.create();
    private MockAPI api;


    @Before
    public void setup() throws IOException {
        context = ShadowApplication.getInstance().getApplicationContext();

        Auth auth = Auth.init(context, BuildConfig.END_POINT);

        //wrap auth retrofit instance
        final MockRetrofit mockRetrofit = new MockRetrofit.Builder(auth.getRetrofit())
                .networkBehavior(behavior)
                .build();

        //delegate net calls to mocked api
        final BehaviorDelegate<Auth.API> delegate = mockRetrofit.create(Auth.API.class);
        api = new MockAPI(delegate);

    }

    //Internal Tests

    @Test
    public void testShouldBeAbleToInitialize() {

        Auth auth = Auth.getInstance();

        assertNotNull("Auth should exists", auth);
        assertNotNull("Auth gson converter should exists", auth.getGson());
        assertNotNull("Auth baseUrl should exists", auth.getBaseUrl());

    }

    @Test
    public void testShouldBeAbleToCheckForConnectivity() {
        Auth auth = Auth.getInstance();

        Boolean isConnected = auth.isConnected();
        assertNotNull("Auth should check for connectivity", isConnected);

    }

    @Test
    public void testShouldBeAbleToValidateCredentials() {

        Boolean isValid = new Auth.Credential(null, null).isValid();
        assertFalse("Should not be valid", isValid);

        isValid = new Auth.Credential("a", null).isValid();
        assertFalse("Should not be valid", isValid);

        isValid = new Auth.Credential(null, "a").isValid();
        assertFalse("Should not be valid", isValid);

        isValid = new Auth.Credential("a", "a").isValid();
        assertFalse("Should not be valid", isValid);

        isValid = new Auth.Credential("a@a.test", "a").isValid();
        assertTrue("Should be valid", isValid);

    }

    @Test
    public void testShouldBeAbleToSetParty() {

        Party joe = new Party("Joe", "don@joe.j", "255714199299");
        Auth auth = Auth.getInstance();

        auth.setParty(joe);
        assertNotNull("Auth should set party", auth.getParty());

        Party party = auth.getParty();
        assertEquals("Party name should exists", joe.getName(), party.getName());
        assertEquals("Party email should exists", joe.getEmail(), party.getEmail());
        assertEquals("Party phone should exists", joe.getPhone(), party.getPhone());
        assertTrue("Token expired should be true", auth.isTokenExpired());

    }

    @Test
    public void testShouldBeAbleToSetPartyWithToken() {

        Party joe =
                new Party("Joe", "don@joe.j", "255714199299", BuildConfig.MAJIFIX_API_TOKEN);
        Auth auth = Auth.getInstance();

        auth.setParty(joe);
        assertNotNull("Auth should set party", auth.getParty());

        Party party = auth.getParty();
        assertEquals("Party name should exists", joe.getName(), party.getName());
        assertEquals("Party email should exists", joe.getEmail(), party.getEmail());
        assertEquals("Party phone should exists", joe.getPhone(), party.getPhone());
        assertFalse("Token expired should be false", auth.isTokenExpired());

    }

    //API Tests
    @Test
    public void testSuccessResponse() {

        Auth.Credential credential = new Auth.Credential("a@a.test", "a");

        givenNetworkFailurePercentIs(0);

        api.signin(credential).subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertComplete();

        //assert value
        Response response = testObserver.values().get(0);
        assertNotNull("Should be able to parse response", response);
        assertTrue("Should be successfully response", response.getSuccess());
        assertNotNull("Should have party", response.getParty());
        assertNotNull("Should have token", response.getToken());
    }

    @Test
    public void testSuccessSignin() {

        final String email = "a@a.test";
        final String password = "a";

        TestObserver<Party> signInObserver = new TestObserver<Party>();

        Auth auth = Auth.getInstance();
        auth.setApi(api);

        auth.signin(email, password).subscribe(signInObserver);

        signInObserver.assertNoErrors();
        signInObserver.assertValueCount(1);

        //assert party
        Party party = signInObserver.values().get(0);
        assertNotNull("Should be able to parse response", party);
        assertNotNull("Should have token", party.getToken());

    }

    @Test
    public void testFailureResponse() throws Exception {
        Auth.Credential credential = new Auth.Credential("a@a.test", "a");

        givenNetworkFailurePercentIs(100);

        api.signin(credential).subscribe(testObserver);

        testObserver.assertNoValues();
        testObserver.assertError(IOException.class);

    }

    //TODO more api test error


    //Auth API Mocking

    public class MockAPI implements Auth.API {

        private final BehaviorDelegate<Auth.API> delegate;

        public MockAPI(BehaviorDelegate<Auth.API> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Observable<Response> signin(Auth.Credential credential) {

            //compose response
            Response response = new Response();
            Party party = new Party("John Doe", "jd@jd.com", "25571665232");
            response.setParty(party);
            response.setToken(BuildConfig.MAJIFIX_API_TOKEN);
            response.setSuccess(true);

            return this.delegate.returningResponse(response).signin(credential);
        }
    }

    private void givenNetworkFailurePercentIs(int failurePercent) {
        this.behavior.setDelay(0, MILLISECONDS);
        this.behavior.setVariancePercent(0);
        this.behavior.setFailurePercent(failurePercent);
    }

}
