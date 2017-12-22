package com.github.codetanzania.open311.android.library.ui.location;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.codetanzania.open311.android.library.ui.BuildConfig;
import com.github.codetanzania.open311.android.library.ui.R;
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

    public static void setStaticMap(ImageView imageView, Location location) {
        if (imageView == null || location == null) {
            return;
        }
        String url = "https://api.mapbox.com/v4/mapbox.streets/" +
                "pin-m-circle+285A98("+location.getLongitude()+","+location.getLatitude()+")/" +
                "auto/" +
                imageView.getWidth() +"x"+ imageView.getHeight() +
                "@2x.png?" +
                "access_token="+ BuildConfig.MAPBOX_TOKEN;

        Glide.with(imageView.getContext()).load(url).into(imageView);
    }
}
