package com.example.majifix311.ui.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.majifix311.BuildConfig;
import com.example.majifix311.R;
import com.example.majifix311.auth.Auth;
import com.example.majifix311.models.Party;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;

/**
 * Reactive Signin activity
 *
 * @see <a href="https://academy.realm.io/posts/donn-felker-reactive-android-ui-programming-with-rxbinding/">https://academy.realm.io/posts/donn-felker-reactive-android-ui-programming-with-rxbinding/</a>
 * @see <a href="https://medium.com/@etiennelawlor/rxjava-on-the-sign-in-screen-9ecb66b88572">https://medium.com/@etiennelawlor/rxjava-on-the-sign-in-screen-9ecb66b88572</a>
 */
public class SigninActivity extends AppCompatActivity {

    public static final String TAG = SigninActivity.class.getSimpleName();

    //view references
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText emailTextInput;
    private TextInputEditText passwordTextInput;
    private Button signInButton;

    //rxbindings
    private CompositeDisposable compositeDisposable;
    private Observable<CharSequence> emailObservable;
    private Observable<CharSequence> passwordObservable;
    private Observable<Object> signInButtonObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //ensure auth initialization
        Auth.init(getApplicationContext(), BuildConfig.END_POINT);

        //obtain view
        emailInputLayout = (TextInputLayout) findViewById(R.id.auth_til_email);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.auth_til_password);
        emailTextInput = (TextInputEditText) findViewById(R.id.auth_tiedt_email);
        passwordTextInput = (TextInputEditText) findViewById(R.id.auth_tiedt_password);
        signInButton = (Button) findViewById(R.id.auth_btn_signin);

        //rx init
        compositeDisposable = new CompositeDisposable();
        observe();
        subscribe();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }


    /**
     * init observables
     */
    private void observe() {
        emailObservable = RxTextView.textChanges(emailTextInput);
        passwordObservable = RxTextView.textChanges(passwordTextInput);
        signInButtonObservable = RxView.clicks(signInButton);
    }

    /**
     * init subscription
     */
    private void subscribe() {

        //observe email text input changes
        Disposable emailDisposable = emailObservable
                .doOnNext(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        hideEmailValidationError();
                    }
                })
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        return !TextUtils.isEmpty(charSequence); // check if not null
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) // Main UI Thread
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        // Check every email input for valid email address
                        if (!isValidInput(charSequence.toString(), 1)) {
                            showEmailValidationError(); // show error for invalid email
                        } else {
                            hideEmailValidationError(); // hide error on valid email
                        }
                    }
                });
        compositeDisposable.add(emailDisposable);

        //observe password text input changes
        Disposable passwordDisposable = passwordObservable
                .doOnNext(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        hidePasswordValidationError();
                    }
                })
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        return !TextUtils.isEmpty(charSequence); // check if not null
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) // Main UI Thread
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        // Check every password input for valid password
                        if (!isValidInput(charSequence.toString(), 2)) {
                            showPasswordValidationError(); // show error for invalid password
                        } else {
                            hidePasswordValidationError(); // hide error on valid password
                        }
                    }
                });
        compositeDisposable.add(passwordDisposable);

        //toggle signin button state base on validity of inputs
        Disposable signInButtonDisposable =
                Observable.combineLatest(emailObservable, passwordObservable,
                        new BiFunction<CharSequence, CharSequence, Boolean>() {
                            @Override
                            public Boolean apply(CharSequence email, CharSequence password) throws Exception {
                                return isValidInput(email.toString(), 1) && isValidInput(password.toString(), 2);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean areValidInputs) throws Exception {
                                if (areValidInputs) {
                                    enableSignInButton(); // enable signin button
                                } else {
                                    disableSignInButton(); // disable signin button
                                }
                            }
                        });
        compositeDisposable.add(signInButtonDisposable);


        //handle signin button clicks
        signInButtonObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        signIn();
                    }
                });
    }

    /**
     * Signin based on current supplied credentials
     */
    private void signIn() {

        //obtain signin credentials
        String email = emailTextInput.getText().toString();
        String password = passwordTextInput.getText().toString();

        //hide keyboard
        hideKeyboard();

        //initialize signin progress dialog
        final ProgressDialog progressDialog =
                new ProgressDialog(SigninActivity.this);
        progressDialog.setMessage(getString(R.string.auth_label_signin));
        progressDialog.setIndeterminate(true);

        //show signin progress dialog
        progressDialog.show();

        Auth auth = Auth.getInstance();
        auth.signin(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Party>() {
                    @Override
                    public void onNext(Party party) {

                        //dismiss progress dialog
                        progressDialog.dismiss();

                        //notify signin succeed
                        showMessage(R.string.auth_success_signin);

                        //TODO fire party over event bus
                    }

                    @Override
                    public void onError(Throwable e) {

                        //dismiss progress dialog
                        progressDialog.dismiss();

                        //notify invalid credentials error
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).code();
                            if (code == 403) {
                                showMessage(R.string.auth_error_invalid_credentials);
                            }
                        }

                        //notify no connection
                        if (e.getCause() != null && e.getCause() instanceof UnknownHostException) {
                            showMessage(R.string.auth_error_not_connected);
                        }

                        //TODO notify other errors

                    }

                    @Override
                    public void onComplete() {
                        //TODO finish signin activity and
                        //TODO navigate to secured ui
                    }

                });
    }


    /**
     * Hide/Show email input validation errors as per case
     *
     * @param hideOrShow: 1 -> for hide , 2 -> for show
     */
    private void hideOrShowEmailValidationError(int hideOrShow) {
        switch (hideOrShow) {
            case 1: // for hide error
                if (emailInputLayout.getChildCount() == 2) {
                    emailInputLayout.getChildAt(1).setVisibility(View.GONE);
                }
                emailInputLayout.setError(null);
                break;
            case 2: // for show error
                if (emailInputLayout.getChildCount() == 2) {
                    emailInputLayout.getChildAt(1).setVisibility(View.VISIBLE);
                }
                emailInputLayout.setError(getString(R.string.auth_error_invalid_email));
                break;
        }
    }

    private void hideEmailValidationError() {
        hideOrShowEmailValidationError(1);
    }

    private void showEmailValidationError() {
        hideOrShowEmailValidationError(2);
    }


    /**
     * Hide/Show password input validation errors as per case
     *
     * @param hideOrShow: 1 -> for hide , 2 -> for show
     */
    private void hideOrShowPasswordValidationError(int hideOrShow) {
        switch (hideOrShow) {
            case 1: // for hide error
                if (passwordInputLayout.getChildCount() == 2) {
                    passwordInputLayout.getChildAt(1).setVisibility(View.GONE);
                }
                passwordInputLayout.setError(null);
                break;
            case 2: // for show error
                if (passwordInputLayout.getChildCount() == 2) {
                    passwordInputLayout.getChildAt(1).setVisibility(View.VISIBLE);
                }
                passwordInputLayout.setError(getString(R.string.auth_error_invalid_password));
                break;
        }
    }

    private void hidePasswordValidationError() {
        hideOrShowPasswordValidationError(1);
    }

    private void showPasswordValidationError() {
        hideOrShowPasswordValidationError(2);
    }


    /**
     * Enable and disable signin button as per case
     *
     * @param enableOrDisable : 1 -> enable , 2 ->  disable
     */
    private void enableOrDisableSignInButton(int enableOrDisable) {
        switch (enableOrDisable) {
            case 1: // enable button
                Drawable activeBackground = ContextCompat.getDrawable(this, R.drawable.enabled_signin_button);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    signInButton.setBackground(activeBackground);
                } else {
                    signInButton.setBackgroundDrawable(activeBackground);
                }
                signInButton.setEnabled(true);
                signInButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                break;
            case 2: // disable button
                Drawable disabledBackground = ContextCompat.getDrawable(this, R.drawable.disabled_signin_button);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    signInButton.setBackground(disabledBackground);
                } else {
                    signInButton.setBackgroundDrawable(disabledBackground);
                }
                signInButton.setEnabled(false);
                signInButton.setTextColor(ContextCompat.getColor(this, R.color.auth_disable_text));
                break;
        }

    }

    private void enableSignInButton() {
        enableOrDisableSignInButton(1);
    }

    private void disableSignInButton() {
        enableOrDisableSignInButton(2);
    }


    /**
     * Validate signin details i.e email and password
     */
    private boolean isValidInput(String input, int whichCase) {

        switch (whichCase) {
            case 1: // check email input
                return !TextUtils.isEmpty(input) && Patterns.EMAIL_ADDRESS.matcher(input).matches();
            case 2: // check password input
                return input.length() >= 6;
        }

        return false;

    }

    /**
     * Notify signin success or failure
     *
     * @param message
     */
    protected void showMessage(String message) {
        Snackbar snackbar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Notify signin success or failure
     *
     * @param resId
     */
    protected void showMessage(int resId) {
        String message = getString(resId);
        showMessage(message);
    }

    /**
     * Hide keyboard
     * TODO use KeyboardUtils
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(passwordTextInput.getWindowToken(), 0);
        }
    }
}
