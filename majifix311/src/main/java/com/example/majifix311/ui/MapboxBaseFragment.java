package com.example.majifix311.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.majifix311.BuildConfig;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.api.geocoding.v5.MapboxGeocoding;

/**
 * This should be used as a base fragment for fragments that wish to use a mapbox view.
 * A DialogFragment is used so that it can either be embedded in an activity or no.
 */

public abstract class MapboxBaseFragment extends DialogFragment implements OnMapReadyCallback {
    protected MapView mMapView;
    protected MapboxMap mMapboxMap;
    protected MapboxGeocoding mGeocoder;
//    protected PermissionsManager mPermissionsGuru;
    protected LocationEngine mLocationEngine;
    protected MarkerOptions mMarker;
    protected LatLng mCurrentLocation;

    private boolean mLocationFoundPreviously;

    protected abstract int getFragLayoutId();

    protected abstract int getMapViewId();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Mapbox.getInstance(context, BuildConfig.MAPBOX_TOKEN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getFragLayoutId(), container, false);

        mMapView = (MapView) rootView.findViewById(getMapViewId());
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
//        if (mLocationEngine != null
//                && ActivityCompat.checkSelfPermission(getContext(),
//                  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getContext(),
//                  Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mLocationEngine.requestLocationUpdates();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
//        if (mLocationEngine != null) {
//            mLocationEngine.removeLocationUpdates();
//        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mGeocoder != null) {
            mGeocoder.cancelCall();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (mPermissionsGuru != null) {
//            mPermissionsGuru.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }

    protected void updateCamera() {
        mMapView.setCameraDistance(10);
        CameraPosition position;
        if (mLocationFoundPreviously) {
            position = new CameraPosition.Builder()
                    .target(mCurrentLocation) // Sets the new camera position
                    .build(); // Creates a CameraPosition from the builder
        }
        else {
            mLocationFoundPreviously = true;
            position = new CameraPosition.Builder()
                    .target(mCurrentLocation) // Sets the new camera position
                    .zoom(15) // Sets the zoom
                    //.bearing(180) // Rotate the camera
                    //.tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder
        }

        mMapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 7000);
    }

    protected void addMarker(LatLng location) {
        mMarker = new MarkerOptions()
                .position(location);
        mMapboxMap.addMarker(mMarker);
    }

    protected void addMarker(LatLng location, Integer titleResId, Integer snippetResId) {
        mMarker = new MarkerOptions()
                .position(location)
                .title(getString(titleResId))
                .snippet(getString(snippetResId));
        mMapboxMap.addMarker(mMarker);
    }

    protected void updateMarker(LatLng location) {
        mMapboxMap.clear();
        addMarker(location);
    }

    protected void updateMarker(LatLng location, Integer titleResId, Integer snippetResId) {
        mMapboxMap.clear();
        addMarker(location, titleResId, snippetResId);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMapboxMap = mapboxMap;
    }

//    TODO: Check permissions using Mapbox
//    protected void checkPermissions(PermissionsListener listener) {
//        mPermissionsGuru = new PermissionsManager(listener);
//        if (!mPermissionsGuru.areLocationPermissionsGranted(getContext())) {
//            mPermissionsGuru.requestLocationPermissions(getActivity());
//        }
//    }
//    TODO: Get location updates with Mapbox
//    protected void getLocationUpdates() {
//        mLocationEngine = LostLocationEngine.getLocationEngine(getContext());
//        mLocationEngine.activate();
//        mLocationEngine.addLocationEngineListener(new LocationEngineListener() {
//            @Override
//            public void onConnected() {
//                if (ActivityCompat.checkSelfPermission(getContext(),
//                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getContext(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                    mLocationEngine.requestLocationUpdates();
//                }
//            }
//
//            @Override
//            public void onLocationChanged(Location location) {
//                onChanged(location);
//            }
//        });
//    }
//
//    protected void onChanged(Location location) {
//
//    }

//    TODO: Figure out how to improve mapbox geocoding for dar es salaam. At present only returns subward
//    public void findCoordinates(String query, Callback<GeocodingResponse> listener) {
//        if (mGeocoder != null) {
//            mGeocoder.cancelCall();
//        }
//        mGeocoder = new MapboxGeocoding.Builder()
//                .setAccessToken(Mapbox.getAccessToken())
//                .setLocation(query)
//                .build();
//
//        mGeocoder.enqueueCall(listener);
//    }
//
//    protected void findAddressWithMapbox(double lat, double lng, Callback<GeocodingResponse> listener) {
//        if (mGeocoder != null) {
//            mGeocoder.cancelCall();
//        }
//        mGeocoder = new MapboxGeocoding.Builder<>()
//                .setAccessToken(Mapbox.getAccessToken())
//                .setCoordinates(Position.fromCoordinates(lng, lat))
//                .build();
//
//        mGeocoder.enqueueCall(listener);
//    }
}
