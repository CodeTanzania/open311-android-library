package com.example.majifix311.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.example.majifix311.BuildConfig;
import com.example.majifix311.R;
import com.example.majifix311.auth.Auth;
import com.example.majifix311.models.Party;


/**
 * Base activity that perform security checks on activity lifecycle
 *
 * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecureActivity extends AppCompatActivity {

    public static final int SIGNIN_REQUEST = 1;

    protected Party party;
    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ensure party exists
        //TODO move to MajiFix initialization
        auth = Auth.init(getApplicationContext(), BuildConfig.END_POINT);

        //check if token expire and direct to auth activity
        Boolean isTokenExpired = auth.isTokenExpired();
        if (isTokenExpired) {
            Intent intent = new Intent(SecureActivity.this, SigninActivity.class);
            startActivityForResult(intent, SIGNIN_REQUEST);
        }

        //set party & continue
        else {
            this.party = auth.getParty();

            //notify party is already present
            onParty();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //handle signin request
        if (requestCode == SIGNIN_REQUEST) {
            if (resultCode == RESULT_OK) {

                //notify signin succeed
                Snackbar snackbar =
                        Snackbar.make(findViewById(android.R.id.content), R.string.auth_success_signin, Snackbar.LENGTH_SHORT);
                snackbar.show();

                //set current party and continue
                this.party = auth.getParty();

                //notify party is already present
                onParty();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Called when party successfully sign in
     */
    protected void onParty() {
    }
}
