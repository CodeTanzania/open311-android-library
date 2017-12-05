//package com.example.majifix311.location;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.mapbox.mapboxsdk.geometry.LatLngBounds;
//import com.mapbox.mapboxsdk.offline.OfflineManager;
//import com.mapbox.mapboxsdk.offline.OfflineRegion;
//import com.mapbox.mapboxsdk.offline.OfflineRegionError;
//import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
//import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
//
//import org.json.JSONObject;
//
//import tz.co.codetanzania.R;
//
///**
// * This is used for downloading Mapbox maps for offline use.
// */
//
//public class MapDownloader implements OfflineManager.CreateOfflineRegionCallback, OfflineRegion.OfflineRegionObserver {
//    private static final String JSON_FIELD_REGION_NAME = "region name";
//    private static final String TAG = "MapDownloader";
//
//    public void downloadTilesForRegion(Context context, String regionName, LatLngBounds bounds) {
//        // Define offline tiles and metadata
//        OfflineTilePyramidRegionDefinition definition =
//                new OfflineTilePyramidRegionDefinition(
//                        context.getResources().getString(R.string.mapbox_style),
//                        bounds, 10, 20, context.getResources().getDisplayMetrics().density);
//        byte[] metadata = createMetadata(regionName);
//
//        // Use OfflineManager to download region
//        OfflineManager offlineManager = OfflineManager.getInstance(context);
//        offlineManager.createOfflineRegion(definition, metadata, this);
//    }
//
//    private byte[] createMetadata(String regionName) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
//            String json = jsonObject.toString();
//            return json.getBytes();
//        } catch (Exception exception) {
//            Log.e("MapboxBaseFragment",
//                    "downloadTilesForRegion: Failed to encode metadata.", exception);
//            return null;
//        }
//    }
//
//    // This is triggered on create
//    @Override
//    public void onCreate(OfflineRegion offlineRegion) {
//        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
//        offlineRegion.setObserver(this);
//    }
//
//    // This is triggered if download fails
//    @Override
//    public void onError(String error) {
//        Log.e(TAG, "Error: " + error);
//    }
//
//    // This gives updates on download status
//    @Override
//    public void onStatusChanged(OfflineRegionStatus status) {
//        double percentage = status.getRequiredResourceCount() >= 0
//                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount())
//                : 0.0;
//        if (status.isComplete()) {
//            Log.d(TAG, "onStatusChanged: Region downloaded Successfully");
//        } else if (status.isRequiredResourceCountPrecise()) {
//            Log.d(TAG, Double.toString(percentage));
//        }
//    }
//
//    // This is triggered if region download fails
//    @Override
//    public void onError(OfflineRegionError error) {
//        Log.e(TAG, "onError: Region download failed. \n" +
//                "Reason: "+error.getReason()+"\n" +
//                "Message: "+error.getMessage());
//    }
//
//    // This is triggered if mapbox doesn't have space
//    @Override
//    public void mapboxTileCountLimitExceeded(long limit) {
//        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
//    }
//}
