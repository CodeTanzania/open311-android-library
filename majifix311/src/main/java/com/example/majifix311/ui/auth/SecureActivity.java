package com.example.majifix311.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.majifix311.BuildConfig;
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

    protected Party party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ensure party exists
        //TODO move to MajiFix initialization
        Auth auth = Auth.init(getApplicationContext(), BuildConfig.END_POINT);

        //check if token expire and direct to auth activity
        Boolean isTokenExpired = auth.isTokenExpired();
        if (isTokenExpired) {
            finish();
            Intent intent = new Intent(SecureActivity.this, AuthActivity.class);
            startActivity(intent);
        }

        //set party & continue
        else {
            this.party = auth.getParty();
        }

    }
}
