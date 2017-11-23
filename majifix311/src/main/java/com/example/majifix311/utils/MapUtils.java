package com.example.majifix311.utils;

import android.content.res.Resources;
import android.location.Location;

import com.example.majifix311.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import java.text.DecimalFormat;


/**
 * This is used for Map functions that can be used throughout the app.
 */

public class MapUtils {
    // TODO Pass this in somehow...
    public static final LatLngBounds DAR_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng( -7.2, 38.9813)) // Northeast
            .include(new LatLng( -6.45, 39.65))  // Southwest
            .build();

    private static DecimalFormat coordinateFormat = new DecimalFormat("#.0000");

    public static String formatCoordinateString(Resources resources, LatLng location) {
        if (location == null) {
            return resources.getString(R.string.default_address_display);
        }
        return String.format(resources.getString(R.string.coordinate_display),
                    coordinateFormat.format(location.getLatitude()),
                    coordinateFormat.format(location.getLongitude()));
    }

//    public static void getStaticMap(Location location) {
//        String url = "https://api.mapbox.com/v4/mapbox.streets/pin-s-marker+285A98(39.2775, -6.8194)/39.2775,-6.8194,19/600x300@2x.png?access_token=pk.eyJ1Ijoia3J0b25nYSIsImEiOiJjajV2ZzAzcDMwMXhlMnFwNGNvZXBucDFsIn0.BxafRKx6aBYMFC-R8x_xkw"
//    }
}
