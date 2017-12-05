package com.github.codetanzania.open311.android.library.location;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.codetanzania.open311.android.library.R;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public final class FetchAddressIntentService extends IntentService {

    // used by logcat
    public static final String TAG = "FetchAddressService";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LATITUDE_DATA_EXTRA";
    public static final String LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LONGITUDE_DATA_EXTRA";

    protected ResultReceiver mReceiver;


    public static void findAddressWithGoogle(Context context, LatLng location, ResultReceiver handler) {
        if (location == null) {
            return;
        }
        findAddressWithGoogle(context, location.getLatitude(), location.getLongitude(), handler);
    }

    public static void findAddressWithGoogle(Context context, Location location, ResultReceiver handler) {
        if (location == null) {
            return;
        }
        findAddressWithGoogle(context, location.getLatitude(), location.getLongitude(), handler);
    }

    public static void findAddressWithGoogle(Context context, double latitude, double longitude, ResultReceiver handler) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, handler);
        intent.putExtra(FetchAddressIntentService.LATITUDE_DATA_EXTRA, latitude);
        intent.putExtra(FetchAddressIntentService.LONGITUDE_DATA_EXTRA, longitude);
        context.startService(intent);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchAddressIntentService() {
        this("FetchAddressIntentService");
    }

    public FetchAddressIntentService(String name) {
        super(name);
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";

        // Get the location passed to this service through an extra.
        if (intent == null) {
            return;
        }
        double latitude = intent.getDoubleExtra(LATITUDE_DATA_EXTRA, 0);
        double longitude = intent.getDoubleExtra(LONGITUDE_DATA_EXTRA, 0);

        // Get Receiver passed through extra
        mReceiver = intent.getParcelableExtra(RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.location_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + latitude +
                    ", Longitude = " + longitude,
                    illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            List<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found)+ Arrays.toString(addressFragments.toArray()));
            deliverResultToReceiver(SUCCESS_RESULT, addressFragments.get(0));
        }
    }

    public static class AddressResultReceiver extends ResultReceiver {
        private Receiver mReciever;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        public void setmReciever(Receiver reciever) {
            this.mReciever = reciever;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.

            // Show a toast message if an address was found.
            if (resultCode == SUCCESS_RESULT) {
                if (mReciever != null) {
                    mReciever.onReceiveAddress(resultData.getString(RESULT_DATA_KEY));
                }
            }

        }
    }

    interface Receiver {
        void onReceiveAddress(String address);
    }
}
