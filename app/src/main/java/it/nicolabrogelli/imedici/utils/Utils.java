package it.nicolabrogelli.imedici.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * Utils is created to set application configuration, from database path, ad visibility.
 */
public class Utils {

    // Debugging tag
    public static final String TAG_PONGODEV             = "Pongodev:";
    public static final String TAG_ACTIVITY_DIRECTIONS  = "ActivityDirection";
    public static final String TAG_ACTIVITY_HOME        = "ActivityHome";
    public static final String TAG_ACTIVITY_CHARRACTERS = "ActivityCharacters";
    public static final String TAG_ACTIVITY_ITINERARIES = "ActivityItineraries";

    // Key values for passing data between activities
    public static final String ARG_LOCATION_ID         = "location_id";
    public static final String ARG_LOCATION_NAME       = "location_name";
    public static final String ARG_LOCATION_ADDRESSES  = "location_addresses";
    public static final String ARG_LOCATION_LONGITUDE  = "location_longitude";
    public static final String ARG_LOCATION_LATITUDE   = "location_latitude";
    public static final String ARG_LOCATION_MARKER     = "location_marker";
    public static final String ARG_LOCATION_ARRAY      = "locations_array";
    public static final String ARG_WAYPOINTS_ARRAY     = "waypoints_array";

    // Key value for passinf data between activites.
    public static final String ARG_ACTIVITY = "activity_return";

    // Key values for ad interstitial trigger
    public static final String ARG_TRIGGER = "trigger";

    // Configurable parameters. you can configure these parameter.
    // Set database path. It must be similar with package name.
    public static final String ARG_DATABASE_PATH = "/data/data/it.nicolabrogelli.imedici/databases/";

    // Set default max distance between current user position and default user position
    public static final float ARG_MAX_DISTANCE = (float) 100.0; // In kilometers

    // Set default max distance between current user position and POI
    public static final int ARG_MAX_DISTANCE_POI = 20;

    // Set default map zoom level. Set value from 1 to 17
    public static final int ARG_DEFAULT_MAP_ZOOM_LEVEL = 10;

    // Set default map type. 0 is normal, 1, is hybrid, 2, is satellite, and 3 is terrain.
    public static final int ARG_DEFAULT_MAP_TYPE = 0;

    // Set default user position if user decide to not enable location
    public static final Double ARG_DEFAULT_LATITUDE  = 43.7800606; //40.768626;
    public static final Double ARG_DEFAULT_LONGITUDE = 11.1707562; //-73.971708;

    // For every ActivityDetail display you want to interstitial ad show up.
    // 3 means interstitial ad will show up after user open ActivityDetail page three times.
    public static final int ARG_TRIGGER_VALUE = 3;

    // Admob visibility parameter. Set true to show admob and false to hide.
    public static final boolean IS_ADMOB_VISIBLE = false;

    // Set value to true if you are still in development process, and false if you are ready to publish the app.
    public static final boolean IS_ADMOB_IN_DEBUG = false;

    // Set Google Map visibility in the detail
    public static final boolean IS_GOOGLEMAPS_VISIBILITY = false;


    // Default is 2500
    public static final Integer ARG_TIMEOUT_MS  = 4000;
    public static final Integer ARG_TIMEOUT = 10;

    // Method to check admob visibility
    public static boolean admobVisibility(AdView ad, boolean isInDebugMode){
        if(isInDebugMode) {
            ad.setVisibility(View.VISIBLE);
            return true;
        }else {
            ad.setVisibility(View.GONE);
            return false;
        }
    }

    // Method to load data that stored in SharedPreferences
    public static int loadPreferences(Context ctx, String param){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_data", 0);
        return sharedPreferences.getInt(param, 0);
    }

    // Method to save data to SharedPreferences
    public static void savePreferences(Context ctx, String param, int value){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(param, value);
        editor.apply();
    }

    /**
     *
     */
    public static void cameraStarrter(Activity activity) {
        Intent i;
        Uri fileUri;
        final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

        // create Intent to take a picture and return control to the calling application
        i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmppicture.jpg";
        File imageFile = new File(imageFilePath);
        fileUri = Uri.fromFile(imageFile); // convert path to Uri
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        i.putExtra("return-data", true);
        // start the image capture Intent
        activity.startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    /**
     *
     * @param context
     * @param emailTo
     * @param emailCC
     * @param subject
     * @param emailText
     * @param filePaths
     */
    public static void sendEmail(Context context, String emailTo, String emailCC,
                                 String subject, String emailText, Uri filePaths){

        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(filePaths);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, "Invia email..."));
    }


}