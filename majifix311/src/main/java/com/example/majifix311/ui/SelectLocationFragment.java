package com.example.majifix311.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.majifix311.R;
import com.example.majifix311.location.FetchAddressIntentService;
import com.example.majifix311.location.LocationTracker;
import com.example.majifix311.utils.KeyboardUtils;
import com.example.majifix311.utils.MapUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode.TRACKING;

/**
 * This creates a location fragment using Mapbox.
 */

public class SelectLocationFragment extends MapboxBaseFragment implements
        LocationTracker.LocationListener,
        //Callback<GeocodingResponse>,
        View.OnClickListener,
        View.OnFocusChangeListener,
        MapboxMap.OnMapLongClickListener {

    private TextInputEditText mAddressView;
    private TextView mCoordinatesView;
    private FloatingActionButton mFabShowCurrent;
    private Button mSubmitButton;
    private Button mEditAddressButton;

    private LocationTracker mLocationTracker;
    //private GeocoderAutoCompleteView mGeocoder;
    private LocationLayerPlugin mLocationLayer;

    private LatLng mUserSelectedPoint;
    private String mAddress;

    /**
     * The interface bridges communication between #SelectLocationFragment,
     * and the context where it is attached. The context must implement this
     * interface in order to receive location coordinates whenever necessary
     */
    private OnSelectLocation mSubmitListener;

    public interface OnSelectLocation {

        /**
         * Interface's only method. The callback is invoked when the current device's
         * location is approximated.
         */
        void selectLocation(double lats, double longs, String address);

    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        try {
            mSubmitListener = (OnSelectLocation) ctx;
        } catch (ClassCastException cce) {
            throw new ClassCastException(ctx.toString() +
                    " must implement SelectLocationFragment#OnSelectLocation interface");
        }
    }

    @Override
    protected int getFragLayoutId() {
        return R.layout.frag_location;
    }

    @Override
    protected int getMapViewId() {
        return R.id.mapView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // expand SelectLocationFragment dialog to fill screen
        setStyle(STYLE_NO_FRAME, getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView != null) {

            mCoordinatesView = (TextView) rootView.findViewById(R.id.tv_Coordinates);
            mCoordinatesView.setText(R.string.default_coordinate_display);

            mAddressView = (TextInputEditText) rootView.findViewById(R.id.tv_Address);
            mAddressView.setText(R.string.default_address_display);
            mAddressView.setOnFocusChangeListener(this);

            mFabShowCurrent = (FloatingActionButton) rootView.findViewById(R.id.fab_PickLocation);
            mFabShowCurrent.setOnClickListener(this);

            mSubmitButton = (Button) rootView.findViewById(R.id.btn_Next);
            mSubmitButton.setOnClickListener(this);

            mEditAddressButton = (Button) rootView.findViewById(R.id.btn_ChangeAddress);
            mEditAddressButton.setOnClickListener(this);

            //setupGeocoder(rootView);
        }
        return rootView;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        super.onMapReady(mapboxMap);

        // This limits map to city of Dar
        mapboxMap.setLatLngBoundsForCameraTarget(MapUtils.DAR_BOUNDS);

        // This tracks users present location
        mLocationTracker = new LocationTracker(getActivity());
        mLocationTracker.start(this);

        // This creates an overlay with a blue dot showing current location
        mLocationLayer = new LocationLayerPlugin(mMapView, mMapboxMap, mLocationEngine);

        // User should be able to long click to select different location
        mMapboxMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Location tracker must be released to avoid memory leaks
        if (mLocationTracker != null) {
            mLocationTracker.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Geocoder must be released to avoid memory leaks
//        if (mGeocoder != null) {
//            mGeocoder.cancelApiCall();
//        }
    }

    // This ensures that GPS is turned on correctly
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLocationTracker != null) {
            mLocationTracker.respondToActivityResult(requestCode, resultCode);
        }
    }

    // This ensures that permission callbacks are handled correctly
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (mLocationTracker != null) {
            mLocationTracker.respondToPermissions(requestCode, grantResults);
        }
    }

    // This is the title of the dialog shown if permission needs to be requested
    @Override
    public String getPermissionAlertTitle() {
        return getString(R.string.location_permission_dialog_title);
    }

    // This is the description of the dialog shown if permission needs to be requested
    @Override
    public String getPermissionAlertDescription() {
        return getString(R.string.location_permission_dialog_description);
    }

    // This is called if turning on location permission or GPS is denied
    @Override
    public void onPermissionDenied() {
        Toast.makeText(getActivity(), R.string.location_permission_denied, Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    // Callback for location tracker, noting changes in user location
    @Override
    public void onLocationChanged(Location location) {
        // Start location layer tracking
        if (mCurrentLocation == null
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationLayer.setLocationLayerEnabled(TRACKING);
        }

        // Update current location
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (mLocationLayer != null) {
            mLocationLayer.forceLocationUpdate(location);
        }
        // If no selected location, assume location is at user location
        if (mUserSelectedPoint == null) {
            updateIssueLocation(mCurrentLocation);
        }
    }

    private void updateIssueLocation(LatLng location) {
        if (getActivity() == null) {
            return;
        }
        mCoordinatesView.setText(MapUtils.formatCoordinateString(getResources(), location));
        showIssueMarker(location);

        // Uses reverse geocoding to get human readable location
        // Unfortunately: Mapbox at this time only returns sub-ward, not full address.
        //   findAddressWithMapbox(location.getLatitude(), location.getLongitude(), this);
        FetchAddressIntentService.findAddressWithGoogle(getActivity().getApplicationContext(), location,
                new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                // here we receive the result
                if (mAddressView != null) {
                    showIssueAddress(resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY));
                }
            }
        });
    }

    private void showIssueAddress(String address) {
        if (address == null || address.isEmpty()
                || address.equals(mAddress)) {
            return;
        }
        // If different than previous address, save and display
        mAddress = address;
        mAddressView.setText(mAddress);
    }

    private void showIssueMarker(LatLng location) {
        if (mMarker == null) {
            updateCamera();
            addMarker(location, R.string.location_marker_title, R.string.location_marker_description);
        } else {
            updateMarker(location, R.string.location_marker_title, R.string.location_marker_description);
        }
    }

    private void switchToUseCurrentLocation() {
        // If necessary, remove user selected location
        if (mUserSelectedPoint != null) {
            mUserSelectedPoint = null;
            // Update fab bar color back to blue
            mFabShowCurrent.setImageResource(R.drawable.ic_location_searching_blue_24dp);
        }
        // Focus map back on current location
        updateCamera();
        // Update marker and address
        updateIssueLocation(mCurrentLocation);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // For reasons of space, hide edit address button when selected
        if (v == mAddressView) {
            if (hasFocus) {
                mEditAddressButton.setVisibility(GONE);
            } else {
                mEditAddressButton.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng point) {
        // Use clicked location as issue location
        mUserSelectedPoint = point;
        mFabShowCurrent.setImageResource(R.drawable.ic_location_searching_black_24dp);
        updateIssueLocation(point);
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmitButton) {
            // TODO Fetching address should not be tied to fragment
            if (mSubmitListener == null || mCurrentLocation == null /*|| mAddress == null */) {
                return;
            }
            // Get location coordinates
            double latitude;
            double longitude;
            if (mUserSelectedPoint == null) {
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();
            } else {
                latitude = mUserSelectedPoint.getLatitude();
                longitude = mUserSelectedPoint.getLongitude();
            }

            // Get location address from edit text in case changes have been made
            mAddress = mAddressView.getText().toString();

            // Submit location to parent activity (or other listener)
            mSubmitListener.selectLocation(latitude, longitude, mAddress);
            dismiss();
        } else if (v == mEditAddressButton) {
            // Select address view and open keyboard
            mAddressView.setFocusableInTouchMode(true);
            mAddressView.requestFocus();
            KeyboardUtils.showSoftInputMethod(getActivity());

        } else if (v == mFabShowCurrent) {
            switchToUseCurrentLocation();
        }
    }

    //TODO use google maps geocoder
//    private void setupGeocoder(View rootView) {
//        mGeocoder = (GeocoderAutoCompleteView) rootView.findViewById(R.id.geoAutoCompleteWidget);
//        mGeocoder.setAccessToken(Mapbox.getAccessToken());
//        mGeocoder.setBbox(38.9813,-7.2,39.65,-6.45);
//        mGeocoder.setTypes(new String[] {GeocodingCriteria.TYPE_COUNTRY ,
//                GeocodingCriteria.TYPE_ADDRESS,
//                GeocodingCriteria.TYPE_DISTRICT,
//                GeocodingCriteria.TYPE_LOCALITY,
//                GeocodingCriteria.TYPE_NEIGHBORHOOD,
//                GeocodingCriteria.TYPE_PLACE,
//                GeocodingCriteria.TYPE_POI,
//                GeocodingCriteria.TYPE_POI_LANDMARK,
//                GeocodingCriteria.TYPE_POSTCODE,
//                GeocodingCriteria.TYPE_REGION});
//        mGeocoder.setOnFeatureListener(new GeocoderAutoCompleteView.OnFeatureListener() {
//            @Override
//            public void onFeatureClick(CarmenFeature feature) {
//                Position position = feature.asPosition();
//                mUserSelectedPoint = new LatLng(position.getLatitude(), position.getLongitude());
//                mAddressView.setText(mUserSelectedPoint.getLatitude() + ", " + mUserSelectedPoint.getLongitude());
//
//                updateCamera();
//                addMarker(new LatLng(mUserSelectedPoint.getLatitude(), mUserSelectedPoint.getLongitude()));
//            }
//        });
//    }

    // NOTE: This is left here as a resource to those who come and look for the mapbox way
    // This is returned from mapbox geocoder to provide human readable location
//    @Override
//    public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
//        List<CarmenFeature> results = response.body().getFeatures();
//        if (results.size() > 0) {
//            showIssueAddress(results.get(0).toString());
//        }
//    }
//
//    // This is returned when mapbox geocoder fails to provide human readable location
//    @Override
//    public void onFailure(Call<GeocodingResponse> call, Throwable t) {
//        // do nothing
//    }
}
