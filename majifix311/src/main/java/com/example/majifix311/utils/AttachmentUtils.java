package com.example.majifix311.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * This works to contain the logic involved in taking pictures using the phones camera
 * as well as retrieving files from the phone file system.
 *
 * For more information, take a look at:
 *  https://developer.android.com/training/camera/photobasics.html#TaskCaptureIntent
 *
 * For information on EXIF (used to fix Samsung photo rotation bug), look here:
 *  http://android-coding.blogspot.com/2011/10/read-exif-of-jpg-file-using.html
 *  https://stackoverflow.com/questions/40864566/how-to-rotate-image-to-its-default-orientation-selected-from-gallery-in-android
 */

public class AttachmentUtils {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 2;

    /** TAKE PICTURE: Step 1 -> Send Intent. This method only returns a thumbnail */
    public static void dipatchTakePictureIntentForThumbnailOnly(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check to ensure that the activity component can handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // This intent will return a bitmap in onActivityResult
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /** TAKE PICTURE: Step 1 (OPTION B) -> Send Intent to take picture and save file.
     * Returns a uri that can be used to retrieve the full size image */
    public static String dipatchTakePictureIntent(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check to ensure that the activity component can handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the image should go
            File photoFile = null;
            try {
                photoFile = createEmptyImageFile(activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // If file was created, start intent with uri. Intent will return in
            // on ActivityResult. No Thumbnail will be sent, and file will need to be parsed.
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(activity,
                        "com.example.majifix311.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                return photoFile.getAbsolutePath();
            }
        }
        return null;
    }

    /** TAKE PICTURE: Step 2 -> get thumbnail. This should be called in onActivityResult.
     * Note: This will only work with dipatchTakePictureIntentForThumbnailOnly */
    public static Bitmap setThumbnailFromActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                return (Bitmap) data.getExtras().get("data");
            }
        }
        return null;    }

    /** TAKE PICTURE: Step 2 (OPTION B) -> This should be called in onActivityResult.
     * This method will handle both the case where the image is saved in a file, and a simple
     * thumbnail sent in the intent. It returns true when bitmap was found successfully. */
    public static boolean setThumbnailFromActivityResult(ImageView imageView, String url,
                                                        int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data == null) {
                // assume image is saved in file
                return setPicFromFile(imageView, url);
            } else {
                // use bitmap sent in intent
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return true;
                }
            }
        }
        return false;
    }

    /** Newer devices may require a permission check. Please call this onRequestPermissionResult in the Activity */
    public static String onRequestPermissionResult(Activity activity, int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return dipatchTakePictureIntent(activity);
                } else {
                    // permission denied... TODO Handle
                }
        }
        return null;
    }

    /** The majiFix api expects a Base64 string as the content for it's attachments */
    public static String getPicAsBase64String(String url) {
        return null;
    }

    /** This extracts a photo from a file to sets it to a given imageview */
    private static boolean setPicFromFile(ImageView view, String uri) {
        if (view == null || uri == null) {
            return false;
        }

        // Get dimens of the view
        int targetW = view.getWidth();
        int targetH = view.getHeight();

        // Get dimens of bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fit the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(uri, bmOptions);

        // Some devices (Samsung) save important rotation info into exif
        // This information must be read, and image manually rotated
        try {
            logExif(uri);
            bitmap = rotateImageBasedOnExifData(bitmap, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set bitmap in view
        System.out.println("Bitmap decoded: "+bitmap);
        view.setImageBitmap(bitmap);
        return true;
    }

    /** Creates an empty file where we want to store photo data */
    private static File createEmptyImageFile(Activity activity) throws IOException {
        // In API 21+, external storage is considered a dangeorus permission that must be checked at runtime
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            System.out.println("File is at: "+storageDirectory.getAbsolutePath());
            File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);
            System.out.println("File is at: "+image.getAbsolutePath());

            // Ensure this file will be accessible to other apps
            addPicToGalleryForExternalUse(activity, image.getAbsolutePath());
            return image;
        } else {
            // TODO Add request permission message
            //if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    //Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show explanation to user async, and try to request again
            //} else {
                // request
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
                // answer will return onRequestPermissionResult in activity
            //}
        }
        return null;
    }

    /** Makes photo available for all apps */
    private static void addPicToGalleryForExternalUse(Context context, String photoAbsolutePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photoAbsolutePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /** Samsung devices store the rotation data in EXIF file, and image must be manually rotated */
    private static Bitmap rotateImageBasedOnExifData(Bitmap bitmap, String photoPath) throws IOException {
        ExifInterface exifInterface = new ExifInterface(photoPath);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }
        return bitmap;
    }

    private static Bitmap rotateImage(Bitmap bitmap, float degrees) {
        System.out.println("Rotating image: "+degrees);
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static void logExif(String file){
        String exif="Exif: " + file;
        try {
            ExifInterface exifInterface = new ExifInterface(file);

            exif += "\nIMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            exif += "\nIMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            exif += "\nGPS related:";
            exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

            System.out.println(exif);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
