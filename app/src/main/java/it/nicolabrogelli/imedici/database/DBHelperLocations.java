package it.nicolabrogelli.imedici.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import it.nicolabrogelli.imedici.models.Itineraries;
import it.nicolabrogelli.imedici.models.Locations;
import it.nicolabrogelli.imedici.models.WayPoints;
import it.nicolabrogelli.imedici.utils.Utils;

/**
 * Design and developed by pongodev.com
 *
 * DBHelperLocations is created to handle location database operation that used within application.
 * Created using SQLiteOpenHelper.
 */
public class DBHelperLocations extends SQLiteOpenHelper {

    // Path of database when app installed on device
    private static String DB_PATH = Utils.ARG_DATABASE_PATH;


    // Create database name and version
    private final static String DB_NAME = "db_locations";
    public final static int DB_VERSION = 1;

    // Create table name and fields
    private final static String TABLE_CATEGORIES = "tbl_categories";
    private final static String CATEGORY_ID = "category_id";
    private final static String CATEGORY_NAME = "category_name";
    private final static String CATEGORY_MARKER = "category_marker";

    // Create table name and fields
    private final static String TABLE_CHARACTERS = "tbl_characters";
    private final static String CHARACTER_ID = "character_id";
    private final static String CHARACTER_NAME = "character_name";
    private final static String CHARACTER_MARKER = "character_marker";

    // Create table name and field
    private final static String TABLE_LOCATIONS = "tbl_locations";
    private final static String LOCATION_ID = "location_id";
    private final static String LOCATION_CATEGORY_ID = "category_id";
    private final static String LOCATION_CHARACTER_ID = "character_id";
    private final static String LOCATION_NAME = "location_name";
    private final static String LOCATION_ADDRESS = "location_address";
    private final static String LOCATION_DESCRIPTION = "location_description";
    private final static String LOCATION_IMAGE = "location_image";
    private final static String LOCATION_LATITUDE = "location_latitude";
    private final static String LOCATION_LONGITUDE = "location_longitude";
    private final static String LOCATION_PHONE = "location_phone";
    private final static String LOCATION_WEBSITE = "location_website";
    private final static String LOCATION_WEBPAGE = "location_webpage";

    //
    private final static String TABLE_ITINERARIES = "tbl_itineraries";
    private final static String ITINERARY_ID = "itinerary_id";
    private final static String ITINERARY_NAME = "itinerary_name";
    private final static String ITINERARY_DESCRIPTION = "itinerary_description";
    private final static String ITINERARY_IMAGE = "itinerary_image";
    private final static String ITINERARY_MARKER = "itinerary_marker";
    private final static String ITINERARY_STARTING_LATITUDE = "itinerary_starting_latitude";
    private final static String ITINERARY_STARTING_LONGITUDE = "itinerary_strating_longitude";
    private final static String ITINERARY_DESTINATION_LATITUDE = "itinerary_destination_latitude";
    private final static String ITINERARY_DESTINATION_LONGITUDE = "itinerary_destination_longitude";

    private final static String TABLE_WAYPOINTS = "tbl_waypoints_itinerary";
    private final static String WAYPOINTS_ID = "waypoint_id";
    private final static String WAYPOINTS_ITINERARY_ID = "itinerary_id";
    private final static String WAYPOINTS_NAMED = "waypoint_name";
    private final static String WAYPOINTS_ADDRESS = "waypoint_address";
    private final static String WAYPOINTS_DESCRIPTION = "waypoint_description";
    private final static String WAYPOINTS_MARKER = "waypoint_marker";
    private final static String WAYPOINTS_LATITUDE = "waypoint_latitude";
    private final static String WAYPOINTS_LONGITUDE = "waypoint_longitude";



    public static SQLiteDatabase db;

    private final Context context;

    /**
     *
     * @param context
     */
    public DBHelperLocations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }


    /**
     * Method to create database
     * @throws IOException
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read;

        // If database exist delete database and copy the new one
        if(dbExist){
            deleteDataBase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }else{
            db_Read = this.getReadableDatabase();
            db_Read.close();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Method to delete database
     */
    private void deleteDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        dbFile.delete();
    }


    /**
     *  Method to check database on path
     * @return
     */
    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    /**
     * Method to copy database from app to db path
     * @throws IOException
     */
    private void copyDataBase() throws IOException{

        InputStream myInput = context.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * Method to open database and read it
     * @throws SQLException
     */
    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * Close database after it is used
     */
    @Override
    public void close() {
        if(db.isOpen()){
            db.close();
        }

    }

    /**
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {}

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}



    /**
     * Method to get all categories data from database
     * @return
     */
    public ArrayList<ArrayList<Object>> getAllCategoriesData(){

        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<ArrayList<Object>>  dataArrays = new ArrayList<>();

        Cursor cursor;

        try{
            cursor = db.query(
                    TABLE_CATEGORIES,
                    new String[]{CATEGORY_ID, CATEGORY_NAME},
                    null, null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));

                    dataArrays.add(dataList);
                    dataList = null;
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }

    /**
     * Method to get all categories data from database
     * @return
     */
    public ArrayList<ArrayList<Object>> getAllCharactersData(){

        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<ArrayList<Object>>  dataArrays= new ArrayList<>();

        Cursor cursor;

        try{
            cursor = db.query(
                    TABLE_CHARACTERS,
                    new String[]{CHARACTER_ID, CHARACTER_NAME},
                    null, null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }

    /**
     * Method to get locations data from database based on category id
     * @param id
     * @return
     */
    public ArrayList<Locations> getObjectLocationsByCharacters(String id) {
        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<Locations> locationses = new ArrayList<>();
        Locations locations = null;
        Cursor cursor = null;
        String query = null;

        if (id.equals("0")){
            query = "SELECT " + LOCATION_ID + ", " + LOCATION_NAME + ", " +
                    LOCATION_ADDRESS + ", " + LOCATION_IMAGE + ", " +
                    LOCATION_LATITUDE + ", " + LOCATION_LONGITUDE + ", " +
                    "c." + CHARACTER_MARKER + " " +
                    "FROM " + TABLE_CHARACTERS + " c, " + TABLE_LOCATIONS + " l " +
                    "WHERE l." + LOCATION_CHARACTER_ID + " = c." + CHARACTER_ID;

            cursor = db.rawQuery(query, null);

        } else {
            query = "SELECT " + LOCATION_ID + ", " + LOCATION_NAME + ", " +
                    LOCATION_ADDRESS + ", " + LOCATION_IMAGE + ", " +
                    LOCATION_LATITUDE + ", " + LOCATION_LONGITUDE + ", " +
                    "c." + CHARACTER_MARKER + " " +
                    "FROM " + TABLE_CHARACTERS + " c, " + TABLE_LOCATIONS + " l ";

            cursor = db.rawQuery(query + " WHERE l." + LOCATION_CHARACTER_ID + " = ? " + " AND l." + LOCATION_CHARACTER_ID + " = c." + CHARACTER_ID ,  new String[]{id});

        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    locations = new Locations.Builder()
                            .setLocationId(cursor.getInt(cursor.getColumnIndex(LOCATION_ID)))
                            .setLocationeName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)))
                            .setLocationAddress(cursor.getString(cursor.getColumnIndex(LOCATION_ADDRESS)))
                            .setLocationImage(cursor.getString(cursor.getColumnIndex(LOCATION_IMAGE)))
                            .setLocationLatitude(cursor.getDouble(cursor.getColumnIndex(LOCATION_LATITUDE)))
                            .setLocationLongitude(cursor.getDouble(cursor.getColumnIndex(LOCATION_LONGITUDE)))
                            .setCategoryMarker(cursor.getString(cursor.getColumnIndex(CHARACTER_MARKER)))
                            .bild();

                    locationses.add(locations);
                } while(cursor.moveToNext());
            }
        }

        cursor.close();
        close();

        return locationses;
    }


    /**
     * Method to get locations data from database based on category id
     * @param id
     * @return
     */
    public ArrayList<Locations> getObjectLocationsByCategory(String id) {
        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<Locations> locationses = new ArrayList<>();
        Locations locations = null;
        Cursor cursor = null;
        String query = null;

        if (id.equals("0")){
            query = "SELECT " + LOCATION_ID + ", " + LOCATION_NAME + ", " +
                    LOCATION_ADDRESS + ", " + LOCATION_IMAGE + ", " +
                    LOCATION_LATITUDE + ", " + LOCATION_LONGITUDE + ", " +
                    "c." + CATEGORY_MARKER + " " +
                    "FROM " + TABLE_CATEGORIES + " c, " + TABLE_LOCATIONS + " l " +
                    "WHERE l." + LOCATION_CATEGORY_ID + " = c." + CATEGORY_ID;

            cursor = db.rawQuery(query, null);

        } else {
            query = "SELECT " + LOCATION_ID + ", " + LOCATION_NAME + ", " +
                    LOCATION_ADDRESS + ", " + LOCATION_IMAGE + ", " +
                    LOCATION_LATITUDE + ", " + LOCATION_LONGITUDE + ", " +
                    "c." + CATEGORY_MARKER + " " +
                    "FROM " + TABLE_CATEGORIES + " c, " + TABLE_LOCATIONS + " l ";

            cursor = db.rawQuery(query + " WHERE l." + LOCATION_CATEGORY_ID + " = ? " + " AND l." + LOCATION_CATEGORY_ID + " = c." + CATEGORY_ID ,  new String[]{id});

        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    locations = new Locations.Builder()
                            .setLocationId(cursor.getInt(cursor.getColumnIndex(LOCATION_ID)))
                            .setLocationeName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)))
                            .setLocationAddress(cursor.getString(cursor.getColumnIndex(LOCATION_ADDRESS)))
                            .setLocationImage(cursor.getString(cursor.getColumnIndex(LOCATION_IMAGE)))
                            .setLocationLatitude(cursor.getDouble(cursor.getColumnIndex(LOCATION_LATITUDE)))
                            .setLocationLongitude(cursor.getDouble(cursor.getColumnIndex(LOCATION_LONGITUDE)))
                            .setCategoryMarker(cursor.getString(cursor.getColumnIndex(CATEGORY_MARKER)))
                            .bild();

                    locationses.add(locations);
                } while(cursor.moveToNext());
            }
        }

        cursor.close();
        close();

        return locationses;
    }


    /**
     *
     * @param id
     * @return
     */
    public Itineraries getObjectItineraryDetailById(String id) {
        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        Itineraries itinerary = null;
        Cursor cursor = null;
        String query = null;

        try {
            query = "SELECT " + ITINERARY_ID + ", " + ITINERARY_NAME +  ", " + ITINERARY_DESCRIPTION + ", " +
                    ITINERARY_IMAGE + ", " + ITINERARY_STARTING_LATITUDE + ", " + ITINERARY_STARTING_LONGITUDE + ", " + ITINERARY_DESTINATION_LATITUDE + ", " + ITINERARY_DESTINATION_LONGITUDE + ", " +
                    ITINERARY_MARKER +
                    " FROM " + TABLE_ITINERARIES;

            cursor = db.rawQuery(query + " WHERE " + ITINERARY_ID + " = ? ",  new String[]{id});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        itinerary = new Itineraries.Builder()
                                .setItineraryId(cursor.getInt(cursor.getColumnIndex(ITINERARY_ID)))
                                .setItineraryName(cursor.getString(cursor.getColumnIndex(ITINERARY_NAME)))
                                .setItineraryDescription(cursor.getString(cursor.getColumnIndex(ITINERARY_DESCRIPTION)))
                                .setItineraryImage(cursor.getString(cursor.getColumnIndex(ITINERARY_IMAGE)))
                                .setItineraryMarker(cursor.getString(cursor.getColumnIndex(ITINERARY_MARKER)))
                                .setItineraryStratingLatitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_STARTING_LATITUDE)))
                                .setItineraryStartingLongitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_STARTING_LONGITUDE)))
                                .setItineraryDestinationLatitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_DESTINATION_LATITUDE)))
                                .setItineraryDestinationLongitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_DESTINATION_LONGITUDE)))
                                .build();
                    } while(cursor.moveToNext());
                }
            }

        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return itinerary;

    }

    /**
     *
     * @param id
     * @return
     */
    public ArrayList<WayPoints> getObjectWayPointsByItineraryId(String id) {
        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<WayPoints> wayPointses = new ArrayList<>();
        Cursor cursor;
        String query;


        try {
            query = "SELECT " + WAYPOINTS_NAMED +  ", " + WAYPOINTS_ADDRESS + ", " +  WAYPOINTS_DESCRIPTION + ", " +
                    WAYPOINTS_LATITUDE + ", " + WAYPOINTS_LONGITUDE + ", " + WAYPOINTS_MARKER +
                    " FROM " + TABLE_WAYPOINTS;

            cursor = db.rawQuery(query + " WHERE " + WAYPOINTS_ITINERARY_ID + " = ? " ,  new String[]{id});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        WayPoints wayPoint = new WayPoints.Builder()
                                .setWayPointName(cursor.getString(cursor.getColumnIndex(WAYPOINTS_NAMED)))
                                .setWayPointAddress(cursor.getString(cursor.getColumnIndex(WAYPOINTS_ADDRESS)))
                                .setWayPointDescription(cursor.getString(cursor.getColumnIndex(WAYPOINTS_DESCRIPTION)))
                                .setWayPointLatitude(cursor.getDouble(cursor.getColumnIndex(WAYPOINTS_LATITUDE)))
                                .setWayPointLongitude(cursor.getDouble(cursor.getColumnIndex(WAYPOINTS_LONGITUDE)))
                                .setWayPointMarker(cursor.getString(cursor.getColumnIndex(WAYPOINTS_MARKER)))
                                .build();

                        wayPointses.add(wayPoint);

                    } while(cursor.moveToNext());
                }
            }
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return wayPointses;

    }

    /**
     *
     * @param id
     * @return
     */
    public Locations getObjectLocationDetailById(String id) {
        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<Locations> locationses = new ArrayList<Locations>();
        Locations locations = null;
        Cursor cursor = null;
        String query;

        try {
            query = "SELECT " + LOCATION_NAME +  ", " + LOCATION_ADDRESS + ", " +  LOCATION_DESCRIPTION + ", " +
                    LOCATION_IMAGE + ", " + LOCATION_LATITUDE + ", " + LOCATION_LONGITUDE + ", " + LOCATION_PHONE + ", " + LOCATION_WEBSITE + ", " + LOCATION_WEBPAGE + ", " +
                    " c." + CATEGORY_MARKER + ", c." + CATEGORY_NAME + " " +
                    " FROM " + TABLE_CATEGORIES + " c, " + TABLE_LOCATIONS + " l ";

            cursor = db.rawQuery(query + " WHERE l." + LOCATION_ID + " = ? " + " AND l." + LOCATION_CATEGORY_ID + " = c." + CATEGORY_ID ,  new String[]{id});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        locations = new Locations.Builder()
                                .setLocationeName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)))
                                .setLocationAddress(cursor.getString(cursor.getColumnIndex(LOCATION_ADDRESS)))
                                .setLocationDescription(cursor.getString(cursor.getColumnIndex(LOCATION_DESCRIPTION)))
                                .setLocationImage(cursor.getString(cursor.getColumnIndex(LOCATION_IMAGE)))
                                .setLocationLatitude(cursor.getDouble(cursor.getColumnIndex(LOCATION_LATITUDE)))
                                .setLocationLongitude(cursor.getDouble(cursor.getColumnIndex(LOCATION_LONGITUDE)))
                                .setLocationPhone(cursor.getString(cursor.getColumnIndex(LOCATION_PHONE)))
                                .setLocationWebSite(cursor.getString(cursor.getColumnIndex(LOCATION_WEBSITE)))
                                .setLocationWebPage(cursor.getString(cursor.getColumnIndex(LOCATION_WEBPAGE)))
                                .setCategoryMarker(cursor.getString(cursor.getColumnIndex(CATEGORY_MARKER)))
                                .setCategoryName(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)))
                                .bild();

                    } while(cursor.moveToNext());
                }
            }
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        cursor.close();
        close();

        return locations;

    }


    /**
     *
     * @return
     */
    public ArrayList<Itineraries> getObjectItineraries() {
        db = this.getReadableDatabase();
        if (db == null) {
            return null;
        }

        ArrayList<Itineraries> intineraries = new ArrayList<>();
        Itineraries itinerary = null;
        Cursor cursor = null;
        String query = null;


        query = "SELECT " + ITINERARY_ID + ", " + ITINERARY_NAME + ", " + ITINERARY_IMAGE + ", " + ITINERARY_MARKER + ", " +
                ITINERARY_DESCRIPTION + ", " + ITINERARY_STARTING_LATITUDE + ", " + ITINERARY_STARTING_LONGITUDE + ", " +
                ITINERARY_DESTINATION_LATITUDE + ", " + ITINERARY_DESTINATION_LONGITUDE + " " +
                "FROM " + TABLE_ITINERARIES;


        cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    itinerary = new Itineraries.Builder()
                            .setItineraryId(cursor.getInt(cursor.getColumnIndex(ITINERARY_ID)))
                            .setItineraryName(cursor.getString(cursor.getColumnIndex(ITINERARY_NAME)))
                            .setItineraryDescription(cursor.getString(cursor.getColumnIndex(ITINERARY_DESCRIPTION)))
                            .setItineraryImage(cursor.getString(cursor.getColumnIndex(ITINERARY_IMAGE)))
                            .setItineraryMarker(cursor.getString(cursor.getColumnIndex(ITINERARY_MARKER)))
                            .setItineraryStratingLatitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_STARTING_LATITUDE)))
                            .setItineraryStartingLongitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_STARTING_LONGITUDE)))
                            .setItineraryDestinationLatitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_DESTINATION_LATITUDE)))
                            .setItineraryDestinationLongitude(cursor.getDouble(cursor.getColumnIndex(ITINERARY_DESTINATION_LONGITUDE)))
                            .build();

                    intineraries.add(itinerary);
                } while(cursor.moveToNext());
            }
        }

        cursor.close();
        close();

        return intineraries;
    }


}
