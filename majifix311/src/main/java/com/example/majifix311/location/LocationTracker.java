package com.example.majifix311.location;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * This is used to contain GLS methods which are used to find current location.
 *
 * *** IMPORTANT ***
 *
 * To avoid memory leaks, do not forget to call myLocationTracker.onPause()
 * in the onPause() Activity lifecycle method. EX:
 *
 *
 *
 * To ensure that permissions is updated correctly, override onRequestPermissionsResult()
 * in Activity. For example:
 *
 *   @Override
 *   public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
 *      mLocationTracker.respondToPermissions(requestCode, grantResults, this);
 *   }
 *
 * To ensure that GPS is turned on correctly, override onActivityResult() in Activity. For example:
 *
 *  @Override
 *  public void onActivityResult(int requestCode, int resultCode, Intent data) {
 *      mLocationTracker.respondToActivityResult(requestCode, resultCode);
 *  }
 */

public class LocationTracker implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener mChangeListener;
    private LocationRequest mLocationRequest;

    public LocationTracker(Activity activity) {
        mActivity = activity;
    }

    public void start(LocationListener listener) {
        mChangeListener = listener;

        // In newest APIs user might manually turn location permission off for this app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (appHasLocationPermission()) {
                buildGoogleApiClient();
            } else {
                askForLocationPermission();
            }
        }
        // In previous APIs permissions are asked for on install
        else {
            buildGoogleApiClient();
        }
    }

    // This must be called in Activity onPause() to avoid memory leaks.
    public void onPause() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    // This is triggered when user attempted to start a LocationTracker when GPS was off
    public void respondToActivityResult(int requestCode, int resultCode) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            onPermissionsChange(resultCode == Activity.RESULT_OK);
        }
    }

    // This is triggered when user attempted to start a LocationTracker when location permission
    // has not been granted to the application
    public void respondToPermissions(int requestCode, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            onPermissionsChange(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    private boolean appHasLocationPermission() {
        return ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    // This is called when GoogleApiClient connects
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = createLocationRequest();

        // For our purposes, we require that GPS is turned on. The following checks if GPS is on,
        // and if not presently enabled prompts the user
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        requestLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // NOTE: Parent Activity & Fragment must override onActivityResult().
                            status.startResolutionForResult(mActivity, MY_PERMISSIONS_REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix it.
                        break;
                }
            }
        });
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            System.out.println("Last location: "+LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private void askForLocationPermission() {
        if (!appHasLocationPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && mChangeListener.getPermissionAlertTitle() != null) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mActivity)
                        .setTitle(mChangeListener.getPermissionAlertTitle())
                        .setMessage(mChangeListener.getPermissionAlertDescription())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(mActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    private void onPermissionsChange(boolean granted) {
        if (granted) {
            if (appHasLocationPermission()) {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                } else {
                    requestLocationUpdates();
                }
            }
        } else {
            if (mChangeListener != null) {
                mChangeListener.onPermissionDenied();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("OnLocationChanged! "+location);
        if (mChangeListener != null) {
            mChangeListener.onLocationChanged(location);
        }
    }

    public interface LocationListener {
        String getPermissionAlertTitle();
        String getPermissionAlertDescription();
        void onLocationChanged(Location location);
        void onPermissionDenied();
    }
}
