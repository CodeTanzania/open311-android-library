//package com.example.majifix311;
//
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.ResultReceiver;
//import android.support.annotation.IdRes;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AlertDialog;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.majifix311.location.FetchAddressIntentService;
//import com.example.majifix311.location.LocationTracker;
//import com.example.majifix311.ui.SelectLocationFragment;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//
///**
// * This is used to find current location.
// */
//
//public class LocationPickerDialog extends DialogFragment implements LocationTracker.LocationListener {
//    private int STATE_SELECT = 0;
//    private int STATE_LOADING = 1;
//    private int STATE_VERIFY = 2;
//
//    private int mSelected = 0;
//    private LocationTracker mLocationTracker;
//
//    private RadioGroup mGroup;
//    private RadioButton mLocationCurrent;
//    private RadioButton mLocationCustom;
//    private ProgressBar mSpinner;
//    private TextView mAddress;
//    private TextView mGpsPoints;
//
//
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        String[] options = {"Use current location", "Select custom location"};
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_location_selector, null);
//
//        mSpinner = (ProgressBar) view.findViewById(R.id.waiting_for_location);
//        mGroup = (RadioGroup) view.findViewById(R.id.location_options);
//        mLocationCurrent = (RadioButton) view.findViewById(R.id.location_current);
//        mLocationCustom = (RadioButton) view.findViewById(R.id.location_custom);
//        mGpsPoints = (TextView) view.findViewById(R.id.gps);
//        mAddress = (TextView) view.findViewById(R.id.address);
//
//        mGroup.getCheckedRadioButtonId();
//        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                if (checkedId == R.id.location_current) {
//                    mLocationTracker = new LocationTracker(getActivity());
//                    mLocationTracker.start(LocationPickerDialog.this);
//                    mSpinner.setVisibility(View.VISIBLE);
//                } else if (checkedId == R.id.location_custom) {
//                    SelectLocationFragment fragment = new SelectLocationFragment();
//                    fragment.show(getFragmentManager(), "select-location-fragment");
//                    getDialog().cancel();
//                }
//            }
//        });
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.hint_location)
//                .setView(view)
//                .setPositiveButton(R.string.action_select, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        System.out.println("Category selected: "+ mGroup.getCheckedRadioButtonId());
//                        onCreateDialog(null);
//                    }
////                })
////                .setNegativeButton(R.string.action_close, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        System.out.println("Dialog canceled");
////                        dismiss();
////                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        return dialog;
//    }
//
//    // This ensures that GPS is turned on correctly
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (mLocationTracker != null) {
//            mLocationTracker.respondToActivityResult(requestCode, resultCode);
//        }
//    }
//
//    // This ensures that permission callbacks are handled correctly
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        if (mLocationTracker != null) {
//            mLocationTracker.respondToPermissions(requestCode, grantResults);
//        }
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        mLocationTracker.onPause();
//    }
//
//    @Override
//    public String getPermissionAlertTitle() {
//        return "GPS Point is necessary to help DAWASCO find the problem.";
//    }
//
//    @Override
//    public String getPermissionAlertDescription() {
//        return "Please allow the app to enable GPS.";
//    }
//
//    @Override
//    public void onLocationChanged(final Location location) {
//        System.out.println("Location found! "+location);
//        mSpinner.setVisibility(View.GONE);
//        mGpsPoints.setText(location.getLatitude()+", "+location.getLongitude());
//        mGpsPoints.setVisibility(View.VISIBLE);
//        FetchAddressIntentService.findAddressWithGoogle(
//                getDialog().getContext(), new LatLng(location), new ResultReceiver(new Handler()) {
//            @Override
//            protected void onReceiveResult(int resultCode, Bundle resultData) {
//                // here we receive the result
//                String address = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
//                mGpsPoints.setText(address + "\n" + location.getLatitude() + ", " + location.getLongitude());
//
//                if (getActivity() instanceof SelectLocationFragment.OnSelectLocation) {
//                    ((SelectLocationFragment.OnSelectLocation) getActivity()).selectLocation(
//                            location.getLatitude(), location.getLongitude(), address);
//                    // close the dialog
//                    getDialog().cancel();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onPermissionDenied() {
//        Toast.makeText(getActivity(), "You will be unable to submit an issue until GPS is enabled",
//                Toast.LENGTH_LONG).show();
//        dismiss();
//    }
//}
