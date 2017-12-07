package com.github.codetanzania.open311.android.library.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.github.codetanzania.open311.android.library.models.Attachment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * This works to contain the logic involved in taking pictures using the phones camera
 * as well as retrieving files from the phone file system.
 *
 *      To take a picture in an Activity, call 'dipatchTakePictureIntent' and be sure to override
 *      'displayOnActivityResult' and 'takePictureOnRequestPermissionResult'.
 *
 *      To open the media store, call `dispatchAddFromGalleryIntent` and be sure to override
 *      'displayOnActivityResult' and 'takePictureOnRequestPermissionResult'.
 *
 * If a UI component is needed that allows user to choose, the AttachmentButton can be used.
 *
 *
 * For more information, take a look at:
 *  https://developer.android.com/guide/topics/media/camera.html#intent-image
 *  https://developer.android.com/training/camera/photobasics.html#TaskCaptureIntent
 *  http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
 *
 * For information on EXIF (used to fix Samsung photo rotation bug), look here:
 *  http://android-coding.blogspot.com/2011/10/read-exif-of-jpg-file-using.html
 *  https://stackoverflow.com/questions/40864566/how-to-rotate-image-to-its-default-orientation-selected-from-gallery-in-android
 *
 *  TODO: Add tests for methods in this class
 */

public class AttachmentUtils {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_BROWSE_MEDIA_STORE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 3;

    private static final int MAX_SIZE = 500;
    private static final int DEFAULT_JPEG_COMPRESSION_QUALITY = 70;

    private static File mCacheDir;

    /**
     * In API 21+, external storage is considered a dangerous permission that must be
     * checked at runtime. This permission will be required by the app
     */
    public static boolean hasPermissions(Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * If app does not have required permissions, ask user to grant them. Required in API 21+
     */
    public static void requestPermissions(Activity activity) {
        // TODO Add request permission message
        //if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
        //Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        // Show explanation to user async, and try to request again
        //} else {
        // request
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_EXTERNAL_STORAGE_PERMISSION);
        // answer will return takePictureOnRequestPermissionResult in activity
        //}
    }

    /**
     * Check if app has the required permissions. Required in API 21+
     */
    public static boolean permissionGranted(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
        }
        return false;
    }

    /**
     * Attachments are too big to pass around, therefore they are saved as files in the
     * cache directory, and a list of urls are saved in the Problem object.
     *
     * IMPORTANT: Client must check for permissions before calling this method.
     */
    public static String saveAttachment(Attachment attachment) {
        // TODO add permission checks for 21+ and above
        if (attachment == null || attachment.getContent() == null) {
            return null;
        }
        FileOutputStream out = null;
        try {
            // Create a file in cache with unique filename
            String suffix = attachment.getMime().split("/")[1];
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String prefix = attachment.getName()+"_" + timeStamp + "_";
            File newFile = File.createTempFile(prefix, suffix, mCacheDir);

            // Save bitmap to file
            out = new FileOutputStream(newFile);
            Bitmap bitmpa = decodeFromBase64String(attachment.getContent());
            if (bitmpa == null) {
                return null;
            }
            bitmpa.compress(Bitmap.CompressFormat.PNG, 100, out);
            return newFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * This extracts a photo from a file to sets it to a given imageview
     */
    public static boolean setPicFromFile(ImageView view, String uri) {
        if (view == null || uri == null) {
            return false;
        }

        // Get dimens of the view
        int targetW = view.getWidth();
        int targetH = view.getHeight();

        // Get scaled bitmap
        Bitmap bitmap = getScaledBitmap(uri, targetW, targetH);
        if (bitmap == null) {
            return false;
        }

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

    /**
     * This helps avoid OOM errors, by loading a bitmap already sized for the imageview
     */
    public static Bitmap getScaledBitmap(String uri, int maxW, int maxH) {
        if (maxW == 0 || maxH == 0) {
            return null;
        }

        // Get dimens of saved bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        System.out.println("Photo w/h: "+photoW+"/"+photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/maxW, photoH/maxH);

        // Decode the image file into a Bitmap sized to fit the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(uri, bmOptions);
    }

    /**
     * The majiFix api expects a Base64 string as the content for it's attachments
     */
    public static Attachment getPicAsAttachment(String url) {
        if (url != null) {
            String mime = getMimeType(url);
            String content = getContentAsBase64String(url);
            System.out.println(content);
            if (content != null) {
                return new Attachment("Photo", "from-app", mime, content);
            }
        }
        return null;
    }

    /**
     * TAKE PICTURE: Step 1 -> Check whether phone has a camera.
     */
    private static boolean phoneHasCamera(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * TAKE PICTURE: Step 2 -> Send Intent. This method only returns a thumbnail, and doesn't
     * save to the filesystem.
     */
    public static void dipatchTakePictureIntentForThumbnailOnly(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check to ensure that the activity component can handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // This intent will return a bitmap in displayOnActivityResult
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * TAKE PICTURE: Step 2 (OPTION B) -> Send Intent to take picture and save file.
     * Returns a uri that can be used to retrieve the full size image
     */
    public static String dipatchTakePictureIntent(Activity activity) {
        // check if phone is equipped with camera
        PackageManager packageManager = activity.getPackageManager();
        if (phoneHasCamera(packageManager)) {
            // check to ensure that the activity component can handle the intent
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the image should go
                File photoFile = null;
                try {
                    photoFile = createEmptyImageFile(activity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    // If file was created, start intent with uri.
                    Uri photoUri = FileProvider.getUriForFile(activity,
                            "com.example.majifix311.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    // Limit photo size to around 1MB TODO Does this work? I think not...
                    takePictureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_SIZE * MAX_SIZE);
                    // Intent will return in on ActivityResult. File will need to be parsed.
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    // Return uri that can be used to parse the photo.
                    return photoFile.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * TAKE PICTURE: Step 3 -> get thumbnail. This should be called in displayOnActivityResult.
     * Note: This will only work with dipatchTakePictureIntentForThumbnailOnly
     */
    public static Bitmap setThumbnailFromActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                return (Bitmap) data.getExtras().get("data");
            }
        }
        return null;
    }


    /**
     * CHOOSE FROM GALLERY: Step 1 -> Send Intent. Url will be in activity result.
     */
    public static void dispatchAddFromGalleryIntent(Activity activity) {
        Intent mediaStoreIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(mediaStoreIntent, REQUEST_BROWSE_MEDIA_STORE);
    }

    /**
     * TAKE PICTURE | CHOOSE FROM GALLERY: Final step -> This should be called in
     * displayOnActivityResult. This method will handle both the case where the image is saved in
     * a file, and a simple thumbnail sent in the intent. It returns the URL String when bitmap
     * was found successfully.
     */
    public static String setThumbnailFromActivityResult(ImageView imageView, String url,
                                                         int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data == null || data.getExtras() == null) {
                // assume image is saved in file
                boolean imageSuccessfullyFound =  setPicFromFile(imageView, url);
                return imageSuccessfullyFound ? url : null;
            } else {
                // use bitmap sent in intent
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return null; //TODO save bitmap
                }
            }
        } else if (requestCode == REQUEST_BROWSE_MEDIA_STORE
                && resultCode == RESULT_OK && data != null) {
            String path = getRealPathFromMediaUri(imageView.getContext(), data.getData());
            boolean success = setPicFromFile(imageView, path);
            return success ? path : null;
        }
        return null;
    }

    /**
     * Newer devices may require a permission check. To Please call this
     * `OnRequestPermissionResult` in the Activity
     */
    public static String takePictureOnRequestPermissionResult(Activity activity, int requestCode, String permissions[], int[] grantResults) {
        if (permissionGranted(requestCode, grantResults)) {
            return dipatchTakePictureIntent(activity);
        } else {
            // permission denied... TODO Handle
        }
        return null;
    }

    /**
     * This is called in `setup` and is the place where attachment files are saved.
     */
    public static void setCacheDirectory(File cacheDirectory) {
        mCacheDir = cacheDirectory;
    }

    /**
     * This returns a bitmap from a Base64 string
     */
    @VisibleForTesting
    public static Bitmap decodeFromBase64String(String content) {
        try {
            byte[] decodedBytes = Base64.decode(content, 0);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            // attachment string was not encoded correctly
            return null;
        }
    }

    /**
     * This is for creating an Attachment object that can be stored in the server
     */
    private static String getContentAsBase64String(String url) {
        // Get scaled bitmap
        Bitmap bitmap = getScaledBitmap(url, MAX_SIZE, MAX_SIZE);

        // Attempt to rotate image
        try {
            bitmap = rotateImageBasedOnExifData(bitmap, url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Compress and encode
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_JPEG_COMPRESSION_QUALITY, byteArrayOS);
            return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }
        return null;
    }

    /**
     * This is for creating an Attachment object that can be stored in the server
     */
    private static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String type = null;
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        // If it's null, lets just assume it's a png
        return type == null ? "image/png" : type;
    }

    /**
     * Creates an empty file where we want to store photo data
     */
    private static File createEmptyImageFile(Activity activity) throws IOException {
        // In API 21+, external storage is considered a dangerous permission that must be checked at runtime
        if (hasPermissions(activity)) {
            File image = createEmptyImageFile();

            // Ensure this file will be accessible to other apps
            addPicToGalleryForExternalUse(activity, image.getAbsolutePath());
            return image;
        } else {
            requestPermissions(activity);
        }
        return null;
    }

    /**
     * This is used to create attachments. The url can then be used to retrieve the image.
     * IMPORTANT: Ensure that permissions are granted before calling this method!
     */
    private static File createEmptyImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".png", storageDirectory);
    }

    /**
     * Makes photo available for all apps //TODO test
     */
    private static void addPicToGalleryForExternalUse(Context context, String photoAbsolutePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photoAbsolutePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * This gets the absolute path from the content provider, for a media asset
     */
    private static String getRealPathFromMediaUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null) {
                return null;
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * This is especially useful for Samsung devices: see `rotateImageBasedOnExifData`
     */
    private static Bitmap rotateImage(Bitmap bitmap, float degrees) {
        System.out.println("Rotating image: "+degrees);
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Samsung devices store the rotation data in EXIF file, and image must be manually rotated
     */
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


//    private static byte[] readFileAsBytes(String url) {
//        File file = new File(url);
//        int size = (int) file.length();
//        byte bytes[] = new byte[size];
//        byte tempBuff[] = new byte[size];
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(file);
//            try {
//                int read = fis.read(bytes, 0, size);
//                if (read < size) {
//                    int remain = size - read;
//                    while (remain > 0) {
//                        read = fis.read(tempBuff, 0, remain);
//                        System.arraycopy(tempBuff, 0, bytes, size - remain, read);
//                        remain -= read;
//                    }
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return bytes;
//    }
}
