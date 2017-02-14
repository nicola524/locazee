package it.nicolabrogelli.imedici.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import it.nicolabrogelli.imedici.R;
import it.nicolabrogelli.imedici.database.DBHelperLocations;
import it.nicolabrogelli.imedici.interfaces.AsyncTaskCompleteListener;
import it.nicolabrogelli.imedici.models.Locations;
import it.nicolabrogelli.imedici.models.Post;
import it.nicolabrogelli.imedici.models.WayPoints;
import it.nicolabrogelli.imedici.task.DownloadPostAsyncTask;
import it.nicolabrogelli.imedici.utils.ImageLoaderFromDrawable;
import it.nicolabrogelli.imedici.utils.MySingleton;
import it.nicolabrogelli.imedici.utils.Utils;

import static android.Manifest.permission.CALL_PHONE;

public class ActivityDetailScrolling extends AppCompatActivity implements
        View.OnClickListener,
        OnMapReadyCallback {

    // Create class objects and variables that required in this class

    private Toolbar toolbar;
    private ImageView imageView;
    private TextView textViewLocationName, textViewLocationCategory, textViewLocationDistance;
    private AdView mAdView;
    private GoogleMap googleMap;
    private LinearLayout btnCall, btnWebsite, btnShare;
    private FloatingActionButton fab;
    private SupportMapFragment mMapFragment;
    private WebView webView;

    private ImageLoaderFromDrawable mImageLoaderFromDrawable;
    private ImageLoader mImageLoader;
    private DBHelperLocations mDBhelper;

    private int mParallaxImageHeight;
    private boolean mIsAdmobVisible;

    private float[] mDistance = new float[1];     // Create float array variable to store distance between location and user position

    private static final int REQUEST_CALL_PHONE = 0;
    private static final String TAG = ActivityDetailScrolling.class.getSimpleName();

    private String mLocationName, mLocationAddress, mLocationImage,
            mLocationDescription, mLocationPhone, mLocationWebPage,
            mLocationWebsite, mLocationMarker, mLocationCategory;

    private Double mLocationLongitude, mLocationLatitude;

    private String mSelectedId;
    private String mReturnStartActivity;

    final String mimeType = "text/html";
    final String encoding = "utf-8";



    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_scrolling);

        // Get dimension value from dimens.xml
        Resources mRes      = getResources();
        int mImageWidth     = mRes.getDimensionPixelSize(R.dimen.flexible_space_image_height);
        int mImageHeight    = mRes.getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mParallaxImageHeight = mRes.getDimensionPixelSize(R.dimen.parallax_image_height);

        // Create database helper object, image loader object, and utils object
        mDBhelper       = new DBHelperLocations(this);
        mImageLoader    = MySingleton.getInstance(this).getImageLoader();
        mImageLoaderFromDrawable = new ImageLoaderFromDrawable(this, mImageWidth, mImageHeight);

        // Get location id that passed from previous activity
        Bundle bundle = getIntent().getExtras();
        mSelectedId     = getIntent().getStringExtra(Utils.ARG_LOCATION_ID);
        mReturnStartActivity = getIntent().getStringExtra(Utils.ARG_ACTIVITY);

        // Connect view objects and view ids in xml
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.image);
        textViewLocationName = (TextView) findViewById(R.id.txtLocationName);
        textViewLocationCategory = (TextView) findViewById(R.id.txtLocationCategory);
        textViewLocationDistance = (TextView) findViewById(R.id.txtLocationDistance);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        btnCall = (LinearLayout) findViewById(R.id.btnCall);
        btnWebsite = (LinearLayout) findViewById(R.id.btnWebsite);
        btnShare = (LinearLayout) findViewById(R.id.btnShare);
        mAdView = (AdView) findViewById(R.id.adView);
        mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        webView = (WebView)findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.setWebChromeClient(new WebChromeClient());


        // Set blank title activity
        this.setTitle("");

        // Call onMapReady to set up map
        mMapFragment.getMapAsync(this);

        // Set toolbar as actionbar,  and add navigation up button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set listener for some view objects
        btnCall.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnWebsite.setOnClickListener(this);
        fab.setOnClickListener(this);


        // Check databases
        checkDatabase();

        // Get location data from database in background using asyntask class
        new SyncGetLocations().execute();

        // Get admob visibility value
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);

        // Load ad in background using asynctask class
        new SyncShowAd(mAdView).execute();


    }


    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    /**
     * Method to handle physical back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Call transition when physical back button pressed
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }


    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Call transition when physical back button pressed
            Intent i;
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            if(mReturnStartActivity.equals(Utils.TAG_ACTIVITY_HOME)) {
                i = new Intent(this, ActivityHome.class);
            }
            else  {
                i = new Intent(this, ActivityCharacters.class);
            }
            startActivity(i);
            Log.d(TAG, "action bar clicked");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnCall:
                makeACall(v);
                break;

            case R.id.btnWebsite:
                // If website address is not available display snackbar,
                // else open browser to access website
                if(isDeviceConnected(getApplicationContext())) {
                    if (mLocationWebsite.equals("-")) {
                        showMessage(v, getString(R.string.no_website_available));
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(mLocationWebsite));
                        startActivity(browserIntent);
                    }
                } else {
                    showMessage(v, "Connessione internte assente..");
                }

                break;

            case R.id.btnShare:
                // Share location info to other apps
                String message = mLocationAddress + "\n" + getString(R.string.phone)+" "+ mLocationPhone +
                        "\n" + mLocationWebsite + "\n\n" + getString(R.string.sent_via_message) + " " +
                        getString(R.string.app_name) + ". " + getString(R.string.download) + " " + getString(R.string.app_name) +
                        " " + getString(R.string.at) + " " + getString(R.string.google_play_url);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mLocationName);
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_to)));

                showMessage(v, "Condividi il punto di interesse.");
                    break;

            case R.id.fab:
                new MaterialDialog.Builder(this)
                        .title(R.string.open_with)
                        .items(R.array.app_list)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which,
                                                    CharSequence text) {
                                switch (which) {
                                    case 0:
                                        // Send location latitude and longitude to Google Maps app
                                        openGoogleMaps();
                                        break;
                                    case 1:

                                        // Or use Locazee to get direction from user position to the location
                                        Intent directionIntent = new Intent(ActivityDetailScrolling.this, ActivityDirection.class);

                                        /*
                                        directionIntent.putExtra(Utils.ARG_LOCATION_NAME, mLocationName);
                                        directionIntent.putExtra(Utils.ARG_LOCATION_ADDRESSES, mLocationAddress);
                                        directionIntent.putExtra(Utils.ARG_LOCATION_LATITUDE, mLocationLatitude);
                                        directionIntent.putExtra(Utils.ARG_LOCATION_LONGITUDE, mLocationLongitude);
                                        directionIntent.putExtra(Utils.ARG_LOCATION_MARKER, mLocationMarker);
                                        directionIntent.putExtra(Utils.ARG_ACTIVITY, mReturnStartActivity);

                                        ArrayList<LatLng> waypoints = new ArrayList<>();
                                        LatLng p = new LatLng(Double.parseDouble(mLocationLatitude),Double.parseDouble(mLocationLongitude));
                                        waypoints.add(p);
                                        directionIntent.putExtra(Utils.ARG_LOCATION_ARRAY, waypoints);
                                        */


                                        ArrayList<WayPoints> wayPointses = new ArrayList<WayPoints>();
                                        WayPoints wayPoint = new WayPoints.Builder()
                                                .setWayPointName(mLocationName)
                                                .setWayPointAddress(mLocationAddress)
                                                .setWayPointLatitude(mLocationLatitude)
                                                .setWayPointLongitude(mLocationLongitude)
                                                .setWayPointMarker(mLocationMarker)
                                                .build();
                                        wayPointses.add(wayPoint);
                                        directionIntent.putExtra(Utils.ARG_WAYPOINTS_ARRAY, wayPointses);
                                        directionIntent.putExtra(Utils.ARG_ACTIVITY, mReturnStartActivity);

                                        startActivity(directionIntent);
                                        overridePendingTransition(R.anim.open_next, R.anim.close_main);

                                        wayPoint = null;
                                        wayPointses = null;

                                        break;
                                }
                            }
                        }).show();

                showMessage(v, "Naviga verso il punto di interesse.");
            break;
        }
    }


    /**
     *
     * @param v
     */
    private void makeACall(View v) {
        // If phone number is not available display snackbar,
        // else make a phone call
        if(mLocationPhone.equals("-")) {
            showMessage(v, getString(R.string.no_phone_available));

        } else {
            if (!RequestCallPhone()) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 8) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mLocationPhone));
                try{
                    startActivity(callIntent);
                }catch (SecurityException e){
                    Log.d(Utils.TAG_PONGODEV + TAG ,""+e.toString());
                }
                Log.d(Utils.TAG_PONGODEV + TAG , "start calling");
            }
        }
    }


    // Implement permissions requests on apps that target API level 23 or higher, and are
    // running on a device that's running Android 6.0 (API level 23) or higher.
    // If the device or the app's targetSdkVersion is 22 or lower, the system prompts the user
    // to grant all dangerous permissions when they install or update the app.
    private boolean RequestCallPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // Checks whether your app has a given permission and whether the app op that corresponds to this permission is allowed.
        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // If already choose deny once
        if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
            askPermissionDialog();
        } else {
            // Requests permissions to be granted to this application.
            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // Call Phone-related task you need to do.
                makeACall(getWindow().getDecorView().getRootView());
                Log.d(Utils.TAG_PONGODEV + TAG, "Request Call Phone Allowed");
            } else {
                // permission was not granted
                if (getApplicationContext() == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
                        makeACall(getWindow().getDecorView().getRootView());
                    } else {
                        permissionSettingDialog();
                    }
                }
            }
        }
    }

    /**
     * Method to display setting dialog
     */
    private void permissionSettingDialog(){
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .backgroundColorRes(R.color.material_background_color)
                .titleColorRes(R.color.primary_text)
                .contentColorRes(R.color.primary_text)
                .positiveColorRes(R.color.accent_color)
                .negativeColorRes(R.color.accent_color)
                .title(R.string.permission)
                .content(R.string.request_call_permission)
                .positiveText(R.string.open_setting)
                .negativeText(android.R.string.cancel)
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().
                                getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        // Close dialog when Cancel button clicked
                        dialog.dismiss();
                    }
                }).build();
        // Show dialog
        dialog.show();
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
                .content(R.string.request_call_phone)
                .positiveText(R.string.open_permission)
                .negativeText(android.R.string.cancel)
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        // Close dialog when Cancel button clicked
                        dialog.dismiss();
                    }
                }).build();

        // Show dialog
        dialog.show();

    }

    /**
     * Method to check whether Google Maps app is installed in user device
     *
     * @return
     */
    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo(
                    "com.google.android.apps.maps", 0 );
            Log.d(Utils.TAG_PONGODEV + TAG,"info= "+info);
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    /**
     * Method to open Google Maps app
     */
    public void openGoogleMaps(){
        // Check whether Google App is installed in user device
        if(isGoogleMapsInstalled())
        {
            // If installed, send latitude and longitude value to the app
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mLocationLatitude + "," +
                    mLocationLongitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        else
        {
            // If not installed, display dialog to ask user to install Google Maps app
            new MaterialDialog.Builder(this)
                    .content(getString(R.string.google_maps_installation))
                    .positiveText(getString(R.string.install))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            try {
                                // Open Google Maps app page in Google Play app
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=com.google.android.apps.maps")
                                ));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(
                                        // Open Google Maps app page in Google Play web
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" +
                                                "com.google.android.apps.maps")
                                ));
                            }
                        }
                    })
                    .show();
        }
    }


    /**
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
    }



    /**
     * Asynctask class to handle load data from database in background
     */
    public class SyncGetLocations extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(Utils.ARG_TRIGGER_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get data from database
            getLocationDataFromDatabase(mSelectedId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String textHtml;
            textHtml = "<strong>" + getString(R.string.address) + "</strong> " +  "<em>" + mLocationAddress + "</em><br /><br />" + mLocationDescription;
            //webView.loadData(textHtml, "text/html", null);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                String base64 = Base64.encodeToString(textHtml.getBytes(), Base64.DEFAULT);
                webView.loadData(base64, "text/html; charset=utf-8", "base64");
            } else {
                String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
                webView.loadData(header + textHtml, "text/html; charset=UTF-8", null);

            }


            textViewLocationName.setText(mLocationName);
            textViewLocationCategory.setText(mLocationCategory);
            String mFinalDistance = String.format("%.2f", (mDistance[0] / 1000)) + " " +
                    getString(R.string.km);
            textViewLocationDistance.setText(mFinalDistance);

            if(mLocationImage.toLowerCase().contains("http")){
                mImageLoader.get(mLocationImage,
                        com.android.volley.toolbox.ImageLoader.getImageListener(imageView,
                                R.mipmap.empty_photo, R.mipmap.empty_photo));

            } else {
                int image = getResources().getIdentifier(mLocationImage, "drawable",
                        getPackageName());
                mImageLoaderFromDrawable.loadBitmap(image, imageView);
            }


            int marker = getResources().getIdentifier(mLocationMarker, "mipmap", getPackageName());
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(mLocationLatitude),
                            Double.valueOf(mLocationLongitude)))
                    .icon(BitmapDescriptorFactory.fromResource(marker))
                    .snippet(mLocationAddress)
                    .title(mLocationName));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.valueOf(mLocationLatitude),
                            Double.valueOf(mLocationLongitude)),
                    Utils.ARG_DEFAULT_MAP_ZOOM_LEVEL);
            googleMap.animateCamera(cameraUpdate);

            // Get the post content
            if(isDeviceConnected(getApplicationContext())) {
                if(!mLocationWebPage.equals("-")) {
                    new DownloadPostAsyncTask(getApplicationContext(), new FetchMyDataTaskCompleteListener()).execute(mLocationWebPage);
                }
            }
        }
    }

    /**
     * Asynctask class to load admob in background
     */
    public class SyncShowAd extends AsyncTask<Void, Void, Void>{

        AdView ad;
        AdRequest adRequest, interstitialAdRequest;
        InterstitialAd interstitialAd;
        int interstitialTrigger;

        public SyncShowAd(AdView ad){
            this.ad = ad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check ad visibility. If visible, create adRequest
            if(mIsAdmobVisible) {
                // Create an ad request
                if (Utils.IS_ADMOB_IN_DEBUG) {
                    adRequest = new AdRequest.Builder().
                            addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }

                // When interstitialTrigger equals ARG_TRIGGER_VALUE, display interstitial ad
                interstitialAd = new InterstitialAd(ActivityDetailScrolling.this);
                interstitialAd.setAdUnitId(ActivityDetailScrolling.this.getResources().getString(R.string.interstitial_ad_unit_id));
                interstitialTrigger = Utils.loadPreferences(getApplicationContext(),
                        Utils.ARG_TRIGGER);
                if(interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    if(Utils.IS_ADMOB_IN_DEBUG) {
                        interstitialAdRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();
                    }else {
                        interstitialAdRequest = new AdRequest.Builder().build();
                    }
                    Utils.savePreferences(getApplicationContext(),Utils.ARG_TRIGGER, 0);
                }else{
                    Utils.savePreferences(getApplicationContext(),Utils.ARG_TRIGGER,
                            (interstitialTrigger+1));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Check ad visibility. If visible, display ad banner and interstitial
            if(mIsAdmobVisible) {
                // Start loading the ad
                ad.loadAd(adRequest);
                if (interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    // Start loading the ad
                    interstitialAd.loadAd(interstitialAdRequest);
                    // Set the AdListener
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {}

                        @Override
                        public void onAdClosed() {}
                    });
                }
            }

        }
    }


    /**
     * Method to check database
     */
    private void checkDatabase(){
        // Create object of database helpers
        mDBhelper = new DBHelperLocations(this);

        // Create database
        try {
            mDBhelper.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open database
        mDBhelper.openDataBase();
    }

    /**
     * Method to retrieve data from database
     * @param id
     */
    public void getLocationDataFromDatabase(String id) {

        Locations locations = mDBhelper.getObjectLocationDetailById(id);
        mLocationName           = locations.getLocationeName();
        mLocationAddress        = locations.getLocationAddress();
        mLocationDescription    = locations.getLocationDescription();
        mLocationImage          = locations.getLocationImage();
        mLocationLatitude       = locations.getLocationLatitude();
        mLocationLongitude      = locations.getLocationLongitude();
        mLocationPhone          = locations.getLocationPhone();
        mLocationWebsite        = locations.getLocationWebSite();
        mLocationWebPage        = locations.getLocationWebPage();
        mLocationMarker         = locations.getCategoryMarker();
        mLocationCategory       = locations.getCategoryName();

        // Get distance between user position and location
        Location.distanceBetween(Double.valueOf(locations.getLocationLatitude().toString()),
                Double.valueOf(locations.getLocationLongitude().toString()),
                ActivityHome.mCurrentLatitude, ActivityHome.mCurrentLongitude, mDistance);

        locations = null;

    }


    /**
     *
     * @param v
     * @param message
     */
    public void showMessage(View v, String message) {
        Snackbar  snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#FF009788"));
        snackbar.show();

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        });
    }

    /**
     *
     */
    public class FetchMyDataTaskCompleteListener implements AsyncTaskCompleteListener<Post>
    {

        @Override
        public void onTaskComplete(Post result)
        {

            final String customHtml = result.get_CONTENT();
            webView.loadDataWithBaseURL("", customHtml + "<style type=\'text/css\'>" + "img{max-width:300px!important; height:auto!important;}" + "</style>", mimeType, encoding, "");

            // do something with the result
        }
    }

    /**
     * controlla se e' presenta la connessione ad internet, sia mobile che wi-fi
     *
     * @param context il context dell'applicazione
     * @return true se il dispositivo e' connesso ad internet (wi-fi o mobile), false altrimenti
     */
    public static Boolean isDeviceConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileDataInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobileDataInfo.getState() != NetworkInfo.State.CONNECTED && wifiInfo.getState() != NetworkInfo.State.CONNECTED) {
            return false;
        }

        return true;
    }

}
