package com.github.codetanzania.open311.android.library.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.github.codetanzania.open311.android.library.BuildConfig;
import com.github.codetanzania.open311.android.library.R;
import com.github.codetanzania.open311.android.library.auth.Auth;
import com.github.codetanzania.open311.android.library.models.Party;


/**
 * Base activity that perform security checks on activity lifecycle
 *
 * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecureCompactActivity extends AppCompatActivity {

    public static final int SIGNIN_REQUEST = 1;

    protected Party party;
    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ensure party exists
        auth = Auth.getInstance();

        //check if token expire and direct to auth activity
        Boolean isTokenExpired = auth.isTokenExpired();
        if (isTokenExpired) {
            Intent intent = new Intent(SecureCompactActivity.this, SigninActivity.class);
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
            } else {
                //TODO notify login required

                //recreate
                recreate();

            }

        }

        //handle other activity request
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    /**
     * Called when party successfully sign in
     */
    protected void onParty() {
    }
}
