package it.nicolabrogelli.imedici.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import it.nicolabrogelli.imedici.R;
import it.nicolabrogelli.imedici.adapters.AdapterLocations;
import it.nicolabrogelli.imedici.database.DBHelperLocations;
import it.nicolabrogelli.imedici.fragments.FragmentDialogError;
import it.nicolabrogelli.imedici.interfaces.OnTapListener;
import it.nicolabrogelli.imedici.listeners.ShakeDetector;
import it.nicolabrogelli.imedici.models.Locations;
import it.nicolabrogelli.imedici.utils.Utils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Design and developed by pongodev.com
 *
 * ActivityHome is created to display locations data in map view and list view.
 * Created using AppCompatActivity.
 */
public class ActivityHome extends AppCompatActivity
        implements ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult>,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    private static final String TAG = ActivityHome.class.getSimpleName();
    private Activity activity;

    // Create view objects
    private RecyclerView mList;
    private Spinner mSpnCategory;
    private FloatingActionButton mFabLocation;
    private SupportMapFragment mMapFragment;
    private AdView mAdView;
    private Menu mMenu;
    private GoogleMap mMap;

    private float maxDistance;

    // Create adapter and dbhelper objects
    private AdapterLocations mAdapter;
    //private AdapterCharacters mAdapter;
    private DBHelperLocations mDBHelper;

    private boolean mIsMapVisible = true;
    private boolean mIsAppFirstLaunched = true;


    // Provides the entry point to Google Play services
    protected GoogleApiClient mGoogleApiClient;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 50000;  //10000

    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Stores parameters for requests to the FusedLocationProviderApi
    protected LocationRequest mLocationRequest;

    // Constant used in the location settings dialog
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // Stores the types of location services the client is interested in using. Used for checking
    // settings to determine if the device has optimal location settings.
    protected LocationSettingsRequest mLocationSettingsRequest;

    // Tracks the status of the location updates request. Value changes when the user presses the
    // Start Updates and Stop Updates buttons.
    protected Boolean mRequestingLocationUpdates = false;

    // Represents a geographical location
    protected Location mCurrentLocation;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;


    // Create arraylist variables to store data
    private ArrayList<String> mLocationIds = new ArrayList<>();
    private ArrayList<String> mLocationNames = new ArrayList<>();
    private ArrayList<String> mLocationImages = new ArrayList<>();
    private ArrayList<String> mLocationAddresses = new ArrayList<>();
    private ArrayList<Float> mLocationDistances = new ArrayList<>();
    private ArrayList<Double> mLocationLatitudes = new ArrayList<>();
    private ArrayList<Double> mLocationLongitudes = new ArrayList<>();
    private ArrayList<String> mLocationMarkers = new ArrayList<>();
    private ArrayList<String> mCategoryIds = new ArrayList<>();
    private ArrayList<String> mCategoryNames = new ArrayList<>();

    // To handle LocationDistance in String
    private ArrayList<String> mLocationDistancesString = new ArrayList<>();

    // Create hashmap variable to store marker id and location id
    private HashMap<String, String> mLocationIdsOnMarkers = new HashMap<>();

    // Create string variable to store category id
    private String mSelectedCategoryId = "0";

    // Create objects and variables for map configuration
    float[] mCheckDistances = new float[1];
    private int mSelectedMapType;
    public static double mCurrentLatitude, mCurrentLongitude;
    private boolean mIsAdmobVisible;
    private int mLocationResultStatus = -1;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final int REQUEST_ACCESS_CAMERA = 1;

    // Flag permission
    private boolean mFlagGranted = true;


    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    AHBottomNavigation bottomNavigation;
    AHBottomNavigationItem itemCamera;
    AHBottomNavigationItem itemNews;
    AHBottomNavigationItem itemItinerari;
    AHBottomNavigationItem itemCharacters;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.activity = ActivityHome.this;

        // Connect view objects with view ids in xml

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mList = (RecyclerView) findViewById(R.id.list);
        mSpnCategory = (Spinner) findViewById(R.id.spnCategory);
        mFabLocation = (FloatingActionButton) findViewById(R.id.fabLocation);
        mAdView = (AdView) findViewById(R.id.adView);
        mMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));


        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        itemCamera = new AHBottomNavigationItem("Foto", R.mipmap.ic_camera_alt_white_24dp, Color.parseColor("#FF009788"));
        itemNews = new AHBottomNavigationItem("News", R.mipmap.ic_new_releases_white_24dp, Color.parseColor("#FF009788"));
        itemCharacters = new AHBottomNavigationItem("Personaggi", R.mipmap.ic_person_outline_white_24dp, Color.parseColor("#FF009788"));
        itemItinerari = new AHBottomNavigationItem("Itinerari", R.mipmap.ic_insert_photo_white_24dp, Color.parseColor("#FF009788"));

        bottomNavigation.addItem(itemCamera);
        bottomNavigation.addItem(itemItinerari);
        bottomNavigation.addItem(itemCharacters);
        bottomNavigation.addItem(itemNews);


        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FF009788"));
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        //  Enables Reveal effect
        bottomNavigation.setColored(true);
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                Snackbar snackbar;
                View sbView;
                Intent i;

                switch (position) {
                    case 0:
                        // Do something cool here...
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                        } else {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                        REQUEST_ACCESS_CAMERA);
                            }
                        }

                        break;

                    case 1:
                        i = new Intent(ActivityHome.this, ActivityItineraries.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        break;


                    case 2:
                        i = new Intent(ActivityHome.this, ActivityCharacters.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        break;

                    case 3:
                        showMessage("Function non implemented");
                        break;

                }
            }
        });


        // Set default map type
        mSelectedMapType = Utils.ARG_DEFAULT_MAP_TYPE;

        // Set up map
        mMapFragment.getMapAsync(this);

        // Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Configure recyclerview
        mList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mList.setLayoutManager(layoutManager);
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1)
                    hideFab();
                else
                    showFab();

            }
        });


        // Hide fab button first
        hideFab();

        // Set listener to fab buttons
        mFabLocation.setOnClickListener(this);


        // Get admob visibility value
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);

        // Load ad in background using asynctask class
        new SyncShowAd(mAdView).execute();

        // Check databases
        checkDatabase();

        // Set adapter object
        mAdapter = new AdapterLocations(this);

        // Get category data
        new SyncGetCategories().execute();

        // Build Google API client. Check if Google play services is available and get user position.
        buildGoogleApiClient();

        // Listener for spinner when item clicked
        mSpnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Save selected category id whenever category change
                mSelectedCategoryId = mCategoryIds.get(i);

                // If this is not the first time of the app run, load data in background
                // using asynctask class
                if (!mIsAppFirstLaunched) {
                    if (mCurrentLocation != null) {
                        new SyncGetLocations().execute();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Listener for recycler view when item clicked
        mAdapter.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(int position) {
                // Open ActivityDetail when item in recyclerview clicked
                Intent detailIntent = new Intent(getApplicationContext(), ActivityDetailScrolling.class);
                detailIntent.putExtra(Utils.ARG_LOCATION_ID, mLocationIds.get(position));
                detailIntent.putExtra(Utils.ARG_ACTIVITY, Utils.TAG_ACTIVITY_HOME);
                startActivity(detailIntent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
            }
        });


        /**
         * Handle item menu in toolbar
         * Disable
         */
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menuView:
                        // Change data view to list view or map view
                        changeView();
                        return true;

                    case R.id.menuAbout:
                        // Open ActivityAbout when about item on toolbar clicked
                        Intent aboutIntent = new Intent(getApplicationContext(), ActivityAbout.class);
                        startActivity(aboutIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    case R.id.menuSetting:
                        // Open ActivitySettings when Setting item on toolbar clicked
                        Intent settingIntent = new Intent(getApplicationContext(), ActivitySettings.class);
                        startActivity(settingIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    default:
                        return true;
                }
            }
        });


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                getUserPosition(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                new SyncGetLocations().execute();
                showFab();

                // Condition after get current location it not search again
                stopLocationUpdates();

            }
        });

    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Method to hide fab buttons with scale animation
     */
    public void hideFab() {
        ViewPropertyAnimator.animate(mFabLocation).cancel();
        ViewPropertyAnimator.animate(mFabLocation).scaleX(0).scaleY(0).setDuration(200).start();
        mFabLocation.setVisibility(View.GONE);
    }

    /**
     * Method to show fab buttons with scale animation
     */
    public void showFab() {
        ViewPropertyAnimator.animate(mFabLocation).cancel();
        ViewPropertyAnimator.animate(mFabLocation).scaleX(1).scaleY(1).setDuration(200).start();
        mFabLocation.setVisibility(View.VISIBLE);
    }

    /**
     * Method to change data view from list view to map view or vise versa
     * This method is temporarily disabled
     */
    public void changeView() {
        // If map is visible hide map and fab buttons. and display recycler view. Else hide recycler
        // view and display map and fab buttons.
        if (mIsMapVisible) {
            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.hide(mMapFragment);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mList.setVisibility(View.VISIBLE);
            mIsMapVisible = false;
            mMenu.getItem(0).setIcon(R.mipmap.ic_map_white_36dp);
            //itemView.setResource(R.mipmap.ic_place_white_24dp);
            //itemView.setTitle("Map");
            bottomNavigation.refreshDrawableState();

            hideFab();
        } else {
            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.show(mMapFragment);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mList.setVisibility(View.GONE);
            mIsMapVisible = true;
            mMenu.getItem(0).setIcon(R.mipmap.ic_view_list_white_36dp);
            //itemView.setResource(R.mipmap.ic_list_white_24dp);
            //itemView.setTitle("List");
            bottomNavigation.refreshDrawableState();

            showFab();
        }
    }


    /**
     * Method to handle click on info window
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        // Open ActivityDetail and pass location id
        String selectedLocationId = mLocationIdsOnMarkers.get(marker.getId());
//        Intent detailIntent = new Intent(this, ActivityDetail.class);
        Intent detailIntent = new Intent(this, ActivityDetailScrolling.class);
        detailIntent.putExtra(Utils.ARG_LOCATION_ID, selectedLocationId);
        detailIntent.putExtra(Utils.ARG_ACTIVITY, Utils.TAG_ACTIVITY_HOME);
        startActivity(detailIntent);
    }

    // Method to set up map
    @Override
    public void onMapReady(GoogleMap googleMap) {

        showMessage(getString(R.string.getting_user_position));

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        setMapType(mSelectedMapType);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        Log.d(Utils.TAG_PONGODEV + TAG, "onMapReady");
    }


    // Asynctask class to load admob in background
    public class SyncShowAd extends AsyncTask<Void, Void, Void> {

        AdView ad;
        AdRequest adRequest;

        public SyncShowAd(AdView ad) {
            this.ad = ad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check ad visibility. If visible, create adRequest
            if (mIsAdmobVisible) {
                // Create an ad request
                if (Utils.IS_ADMOB_IN_DEBUG) {
                    adRequest = new AdRequest.Builder().
                            addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Check ad visibility. If visible, display ad banner and interstitial
            if (mIsAdmobVisible) {
                // Start loading the ad
                ad.loadAd(adRequest);

            }

        }
    }

    //Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null && checkGooglePlayService()) {
            Log.d(Utils.TAG_PONGODEV + TAG, "Building GoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            /**
             * Sets up the location request. Android has two location request settings:
             * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings
             * control the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION,
             * as defined in the AndroidManifest.xml.
             * <p/>
             * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
             * interval (5 seconds), the Fused Location Provider API returns location updates
             * that are accurate to within a few feet.
             * <p/>
             * These settings are appropriate for mapping applications that show real-time location
             * updates.
             */
            mLocationRequest = new LocationRequest();

            // Sets the desired interval for active location updates. This interval is
            // inexact. You may not receive updates at all if no location sources are available, or
            // you may receive them slower than requested. You may also receive updates faster than
            // requested if other applications are requesting location at a faster interval.
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);


            // Sets the fastest rate for active location updates. This interval is exact, and your
            // application will never receive updates faster than this value.
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            /**
             * Uses a {@link LocationSettingsRequest.Builder} to build
             * a {@link LocationSettingsRequest} that is used for checking
             * if a device has the needed location settings.
             */
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);
            mLocationSettingsRequest = builder.build();

            /**
             * Check if the device's location settings are adequate for the app's needs using the
             * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings
             * (GoogleApiClient,LocationSettingsRequest)} method,
             *  with the results provided through a {@code PendingResult}.
             */
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiClient,
                            mLocationSettingsRequest
                    );
            result.setResultCallback(this);

        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, 0);
            if (dialog != null) {
                FragmentDialogError errorFragment = new FragmentDialogError();
                errorFragment.setDialog(dialog);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(errorFragment, null);
                ft.commitAllowingStateLoss();
            }
        }

    }

    //**** Start: Setting Location ****//
    // Check google play service if available run map
    private boolean checkGooglePlayService() {
        /**
         * verify that Google Play services is available before making a request.
         *
         * @return true if Google Play services is available, otherwise false
         */
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(Utils.TAG_PONGODEV + TAG, getString(R.string.play_services_available));
            return true;

            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                FragmentDialogError errorFragment = new FragmentDialogError();
                errorFragment.setDialog(dialog);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(errorFragment, null);
                ft.commitAllowingStateLoss();
            }
            return false;
        }
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d(Utils.TAG_PONGODEV + TAG, "All location settings are satisfied.");
                if (mCurrentLocation == null) {
                    Log.d(Utils.TAG_PONGODEV + TAG, "onResult SUCCESS mCurrentLocation == null");
                    
                    mCurrentLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient);

                }
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d(Utils.TAG_PONGODEV + TAG,
                        "Location settings are not satisfied. Show the user a dialog to" +
                                "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(ActivityHome.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.d(Utils.TAG_PONGODEV + TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d(Utils.TAG_PONGODEV + TAG,
                        "Location settings are inadequate, and cannot be fixed here. Dialog " +
                                "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mLocationResultStatus = resultCode;
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            // If OK selected, then update user location, else if no button selected use default
            // location.
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // If GPS enabled, start location update to get user position
                        Log.d(Utils.TAG_PONGODEV + TAG,
                                "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // If GPS not enabled, set default user position
                        mCurrentLocation = new Location("");
                        mCurrentLocation.setLatitude(Utils.ARG_DEFAULT_LATITUDE);
                        mCurrentLocation.setLongitude(Utils.ARG_DEFAULT_LONGITUDE);
                        mCurrentLatitude = Utils.ARG_DEFAULT_LATITUDE;
                        mCurrentLongitude = Utils.ARG_DEFAULT_LONGITUDE;

                        getUserPosition(mCurrentLatitude, mCurrentLongitude);
                        new SyncGetLocations().execute();
                        showFab();
                        break;
                }
                break;


            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Image captured and saved to fileUri specified in the Intent
                    Utils.sendEmail(this, "nicola524@hotmail.it", "", "Nuovo punto di interesse", "Invio la posizione e la foto del nuovo POI", fileUri);
                    showMessage("Image saved to:\n" + fileUri.getPath().toString());

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                    showMessage("User cancelled the image capture");
                } else {
                    // Image capture failed, advise user
                    showMessage("Image capture failed, advise user");
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(Utils.TAG_PONGODEV + TAG, "Connection suspended");
    }

    /*
     * called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Utils.TAG_PONGODEV + TAG, "onConnectionFailed");
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        9000);

                /*
                * thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
                Log.i("onConnectionFailed", "" + e);
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, 0);
            if (dialog != null) {
                FragmentDialogError errorFragment = new FragmentDialogError();
                errorFragment.setDialog(dialog);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(errorFragment, null);
                ft.commitAllowingStateLoss();
            }
        }
    }


    protected void startCamera() {
        showMessage("Camera started");

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmppicture.jpg";
        File imageFile = new File(imageFilePath);
        fileUri = Uri.fromFile(imageFile); // convert path to Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        intent.putExtra("return-data", true);
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // When request permission mFlagGranted false
        mFlagGranted = false;
        if (!mayRequestLocations()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 8) {

            mMap.setMyLocationEnabled(true);
            // after get permission mFlagGranted true
            mFlagGranted = true;

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        mLocationRequest,
                        this
                ).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mRequestingLocationUpdates = true;
                    }
                });
            }
            catch(IllegalStateException e) {}

            Log.d(Utils.TAG_PONGODEV + TAG, "startLocationUpdates");
        }
    }

    // Implement permissions requests on apps that target API level 23 or higher, and are
    // running on a device that's running Android 6.0 (API level 23) or higher.
    // If the device or the app's targetSdkVersion is 22 or lower, the system prompts the user
    // to grant all dangerous permissions when they install or update the app.
    private boolean mayRequestLocations() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // If already choose deny once
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            askPermissionDialog();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
        return false;

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // location-related task you need to do.
                startLocationUpdates();
                Log.d(Utils.TAG_PONGODEV + TAG, "Request Location Allowed");
            } else {
                // permission was not granted
                if (getApplicationContext() == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                        startLocationUpdates();
                    } else {
                        permissionSettingDialog();
                    }
                }
            }
        }
        else if(requestCode == REQUEST_ACCESS_CAMERA) {
            startCamera();
        }

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    // Method to set map type based on dialog map type
    private void setMapType(int position) {
        switch (position) {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }
    //**** End: Setting Location ****//

    // Method to check database
    private void checkDatabase() {
        // Create object of database helpers
        mDBHelper = new DBHelperLocations(this);

        // Create database
        try {
            mDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        // Open recipes database
        mDBHelper.openDataBase();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabLocation:
                // Get user position
                mCurrentLatitude = mCurrentLocation.getLatitude();
                mCurrentLongitude = mCurrentLocation.getLongitude();
                getUserPosition(mCurrentLatitude, mCurrentLongitude);

                //if (mCurrentLocation != null) {
                    new SyncGetLocations().execute();
                //}
            break;
        }
    }

    // Method to get user position
    public void getUserPosition(double latitude, double longitude){
        // Check distance between user position and default position
        Location.distanceBetween(latitude, longitude,
                Utils.ARG_DEFAULT_LATITUDE, Utils.ARG_DEFAULT_LONGITUDE, mCheckDistances);


        if ((mCheckDistances[0] / 1000) > Utils.ARG_MAX_DISTANCE) {
            mCurrentLocation = new Location("");
            mCurrentLocation.setLatitude(Utils.ARG_DEFAULT_LATITUDE);
            mCurrentLocation.setLongitude(Utils.ARG_DEFAULT_LONGITUDE);
            mCurrentLatitude = mCurrentLocation.getLatitude();
            mCurrentLongitude = mCurrentLocation.getLongitude();

            if(mLocationResultStatus == Activity.RESULT_CANCELED){
                showMessage(getString(R.string.gps_not_enabled_alert));

            }else {
                showMessage(getString(R.string.distance_alert));
            }
        }else{
            mCurrentLatitude = latitude;
            mCurrentLongitude = longitude;
        }

        // Move camera to user position
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLatitude, mCurrentLongitude),
                Utils.ARG_DEFAULT_MAP_ZOOM_LEVEL);
        mMap.animateCamera(cameraUpdate);

    }



    // Method to display map type dialog
    public void showMapTypeDialog() {
        new MaterialDialog.Builder(this)
            .title(R.string.dialog_map_type_title)
            .items(R.array.map_types)
            .itemsCallbackSingleChoice(mSelectedMapType,
                    new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog,
                                           View view, int selectedIndex, CharSequence text) {
                    mSelectedMapType = selectedIndex;
                    setMapType(mSelectedMapType);
                    return true;
                }
            }).positiveText(R.string.select)
            .show();
    }

    // Asynctask class to get category data from database
    public class SyncGetCategories extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Clear arraylist variable first before used
            mCategoryIds.clear();
            mCategoryNames.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get category data from database
            getCategoryFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Set category data to spinner
            ArrayAdapter<String> categoryAdapter;
            categoryAdapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    R.layout.layout_spinner,
                    mCategoryNames
            );
            categoryAdapter.setDropDownViewResource(R.layout.layout_spinner);
            mSpnCategory.setAdapter(categoryAdapter);
        }
    }


    /**
     * Method to get data from database
     */
    public void getCategoryFromDatabase(){
        ArrayList<ArrayList<Object>> dataCategory = mDBHelper.getAllCategoriesData();

        // Ad "All Places" in first row
        mCategoryIds.add("0");
        mCategoryNames.add(getString(R.string.all_places));
        for(int i = 0; i < dataCategory.size(); i++){
            ArrayList<Object> row = dataCategory.get(i);

            mCategoryIds.add(row.get(0).toString());
            mCategoryNames.add(row.get(1).toString());
            row = null;
            dataCategory.set(i, null);
        }

    }

    // Asynctask class to load location data from database
    public class SyncGetLocations extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // If this is not the first time app launched, set up and display progress dialog
            if(!mIsAppFirstLaunched) {
                showMessage(getString(R.string.loading_data));
            }else{
                showMessage(getString(R.string.loading_data));
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get data from database
            getLocationDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Set data to adapter
            mAdapter.updateList(mLocationIds, mLocationNames,
                    mLocationAddresses, mLocationDistancesString, mLocationImages);

            // Set adapter to recycler view
            mList.setAdapter(mAdapter);

            // if this is the first time app launched, than set it to false
            if(mIsAppFirstLaunched){
                mIsAppFirstLaunched = false;
            }

            // Set up map after getting location data from database
            setupMarker();
        }
    }

    /**
     * Method to retrieve locations data from database
     */
    private void getLocationDataFromDatabase(){
        // Clear arraylist variables first before storing data
        mLocationIds.clear();
        mLocationNames.clear();
        mLocationImages.clear();
        mLocationAddresses.clear();
        mLocationLatitudes.clear();
        mLocationLongitudes.clear();
        mLocationDistances.clear();
        mLocationMarkers.clear();
        mLocationDistancesString.clear();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int perfereceMaxDistance = preferences.getInt("pref_range_km", 0);
        boolean preferenceMaxDistanceEnabled = preferences.getBoolean("pref_enabled_range_km", false);
        int maxDistancePoi = 0;

        // Store data to arraylist variables
        ArrayList<Locations> dataLocation = mDBHelper.getObjectLocationsByCategory(mSelectedCategoryId);

        float[] distances = new float[1];

        for(int i = 0; i< dataLocation.size(); i++){
            Locations locations = dataLocation.get(i);

            // Check distance between locations and user position first
            Location.distanceBetween(Double.valueOf(locations.getLocationLatitude().toString()),
                    Double.valueOf(locations.getLocationLongitude().toString()),
                    mCurrentLatitude, mCurrentLongitude, distances);


            if(preferenceMaxDistanceEnabled) {
                if(perfereceMaxDistance > 0 ) {
                    maxDistancePoi = perfereceMaxDistance;
                }
                else {
                    maxDistancePoi = Utils.ARG_MAX_DISTANCE_POI;
                }
            }
            else
                maxDistancePoi = Utils.ARG_MAX_DISTANCE_POI;

            if(maxDistancePoi > (distances[0] / 1000.0)) {
                mLocationIds.add(String.valueOf(locations.getLocationId()));
                mLocationNames.add(locations.getLocationeName());
                mLocationAddresses.add(locations.getLocationAddress());
                mLocationImages.add(locations.getLocationImage());
                mLocationLatitudes.add(locations.getLocationLatitude());
                mLocationLongitudes.add(locations.getLocationLongitude());
                mLocationMarkers.add(locations.getCategoryMarker());

                float paramDistance = (distances[0] / 1000);
                mLocationDistances.add(paramDistance);

                // For trigger variable mLocationDistancesString
                mLocationDistancesString.add(String.format("%.2f", paramDistance));
            }

            locations = null;
            dataLocation.set(i, null);

        }

        // Sort data base on distance between location and user position
        sortDataByDistance();
        distances = null;
    }

    /**
     * Method to set up location markers
     */
    private void setupMarker(){
        // Clear map before displaying marker
        mMap.clear();

        // Add marker to all locations
        for(int i = 0; i< mLocationLatitudes.size(); i++){

            int marker = getResources().getIdentifier(
                    mLocationMarkers.get(i), "mipmap", getPackageName());

            Marker locationMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLocationLatitudes.get(i), mLocationLongitudes.get(i)))
                    .icon(BitmapDescriptorFactory.fromResource(marker))
                    .snippet(mLocationAddresses.get(i))
                    .title(mLocationNames.get(i)));

            mLocationIdsOnMarkers.put(locationMarker.getId(), mLocationIds.get(i));

        }

        if(mLocationLatitudes.size() > 0) {
            showMessage("Sono stati trovati " + mLocationLatitudes.size());
        }
        else {
            showMessage("Non sono stati trovati POI per la selezione effettuata");
        }

    }

    /**
     * Method to sort data by distance
     */
    private void sortDataByDistance()    {
        int j;
        boolean flag = true;   // set flag to true to begin first pass
        Float tempDistance;   //holding variable
        String tempId, tempName,  tempAddress, tempImage, tempMarker;
        Double tempLatitude, tempLongitude;
        while ( flag )
        {
            flag= false;    //set flag to false awaiting a possible swap
            for( j=0;  j < mLocationDistances.size()-1;  j++ )
            {
                // change to > for ascending sort, < for descending
                if ( mLocationDistances.get(j)> mLocationDistances.get(j+1) )
                {
                    //swap elements
                    tempDistance    = mLocationDistances.get(j);
                    tempId          = mLocationIds.get(j);
                    tempName        = mLocationNames.get(j);
                    tempLatitude    = mLocationLatitudes.get(j);
                    tempLongitude   = mLocationLongitudes.get(j);
                    tempAddress     = mLocationAddresses.get(j);
                    tempImage       = mLocationImages.get(j);
                    tempMarker      = mLocationMarkers.get(j);

                    mLocationDistances.set(j, mLocationDistances.get(j + 1));

                    // Setting mLocationDistancesString in string format with 2 decimal
                    mLocationDistancesString.set(j, String.format("%.2f",
                            mLocationDistances.get(j + 1)));

                    mLocationIds.set(j, mLocationIds.get(j + 1));
                    mLocationNames.set(j, mLocationNames.get(j + 1));
                    mLocationLatitudes.set(j,mLocationLatitudes.get(j+1) );
                    mLocationLongitudes.set(j,mLocationLongitudes.get(j+1) );
                    mLocationAddresses.set(j,mLocationAddresses.get(j+1) );
                    mLocationImages.set(j,mLocationImages.get(j+1) );
                    mLocationMarkers.set(j,mLocationMarkers.get(j+1) );

                    mLocationDistances.set(j + 1, tempDistance);
                    mLocationDistancesString.set(j + 1, String.format("%.2f", tempDistance));

                    mLocationIds.set(j+1,tempId);
                    mLocationNames.set(j+1,tempName);
                    mLocationLatitudes.set(j+1,tempLatitude);
                    mLocationLongitudes.set(j+1,tempLongitude);
                    mLocationAddresses.set(j+1,tempAddress);
                    mLocationImages.set(j+1,tempImage);
                    mLocationMarkers.set(j+1,tempMarker);

                    flag = true;              //shows a swap occurred
                } else {
                    // Setting mLocationDistancesString in string format with 2 decimal
                    mLocationDistancesString.set(j, String.format("%.2f", mLocationDistances.get(j)));
                }
            }
        }
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_home, menu);
        mMenu = menu;
        return true;
    }

    /**
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;


        if(mIsAppFirstLaunched) {

            getUserPosition(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            new SyncGetLocations().execute();
            showFab();

            // Condition after get current location it not search again
            stopLocationUpdates();
        }


        //getUserPosition(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //new SyncGetLocations().execute();
        //showFab();
    }

    /**
     * Method to display share dialog
     */
    private void askPermissionDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .backgroundColorRes(R.color.material_background_color)
            .titleColorRes(R.color.primary_text)
            .contentColorRes(R.color.primary_text)
            .positiveColorRes(R.color.accent_color)
            .negativeColorRes(R.color.accent_color)
            .title(R.string.permission)
            .content(R.string.request_location)
            .positiveText(R.string.open_permission)
            .negativeText(R.string.close_app)
            .cancelable(false)
            .callback(new MaterialDialog.ButtonCallback() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onPositive(MaterialDialog dialog) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                            REQUEST_ACCESS_FINE_LOCATION);
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    // Close dialog when Cancel button clicked
                    finish();
                }
            }).build();
        // Show dialog
        dialog.show();
    }

    /**
     * Method to display setting dialog
     */
    private void permissionSettingDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .backgroundColorRes(R.color.material_background_color)
                .titleColorRes(R.color.primary_text)
                .contentColorRes(R.color.primary_text)
                .negativeColorRes(R.color.accent_color)
                .title(R.string.permission)
                .content(R.string.request_location_permission)
                .negativeText(R.string.close_app)
            .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        // Close dialog when Cancel button clicked
                        finish();
                }
            }).build();
        // Show dialog
        dialog.show();
    }

    /**
     *
     * @param message
     */
    public void showMessage(String message) {
        Snackbar  snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#FF009788"));
        snackbar.show();

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                bottomNavigation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                bottomNavigation.setVisibility(View.GONE);
            }
        });
    }


    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Utils.TAG_PONGODEV + TAG, "onStart");
        if(checkGooglePlayService()) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }

    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
        Log.d(Utils.TAG_PONGODEV + TAG, "onPause");
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if(checkGooglePlayService()) {
            // Stop location updates to save battery,
            // but don't disconnect the GoogleApiClient object.
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }

    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);

        Log.d(Utils.TAG_PONGODEV + TAG, "onResume");

        if (mAdView != null) {
            mAdView.resume();
        }
        if(checkGooglePlayService()) {
            if(mGoogleApiClient.isConnected() && mFlagGranted){
                startLocationUpdates();
            }
        }
    }

    /**
     *
     */
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(mShakeDetector);
        Log.d(Utils.TAG_PONGODEV + TAG, "onStop");
            if(checkGooglePlayService()) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mShakeDetector);
        Log.d(Utils.TAG_PONGODEV + TAG, "onDestroy");

        if(checkGooglePlayService()) {
            // Stop location updates to save battery,
            // but don't disconnect the GoogleApiClient object.
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }

        if (mAdView != null) {
            mAdView.destroy();
        }
        mDBHelper.close();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(Utils.TAG_PONGODEV + TAG, "Wait until user position");
    }

}

