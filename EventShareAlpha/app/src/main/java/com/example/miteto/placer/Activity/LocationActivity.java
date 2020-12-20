package com.example.miteto.placer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miteto.placer.DTO.PlaceDTO;
import com.example.miteto.placer.DTO.UserDTO;
import com.example.miteto.placer.Helper.Constants;
import com.example.miteto.placer.Helper.GeofenceErrorMessages;
import com.example.miteto.placer.Helper.XMLParser;
import com.example.miteto.placer.PlaceDTOArray;
import com.example.miteto.placer.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LocationActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>
{
    protected static final String TAG = "LocationActivity";

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView city_text;

    private ProgressDialog progress ;
    private Geocoder geocoder;
    private String cityName = "";
    private UserDTO userDTO;

    static final String KEY_PLACE = "place";
    static final String KEY_NAME = "name";
    static final String KEY_ICON = "icon";
    static final String KEY_TIME = "time";
    static final String KEY_LAT = "lat";
    static final String KEY_LON = "lon";
    static final String KEY_RADIUS = "radius";

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Intent intent = getIntent();
        if(intent.hasExtra("user"))
        userDTO = intent.getParcelableExtra("user");

        //mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        //mLongitudeText = (TextView) findViewById((R.id.longitude_text));
        //city_text = (TextView) findViewById((R.id.city_text));

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        // Get the geofences used. Geofence data is hard coded in this sample.


        buildGoogleApiClient();

        geocoder = new Geocoder(this, Locale.getDefault());

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        progress.show();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
        progress.dismiss();
    }


    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (mLastLocation != null)
        {

            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            cityName = getCity();
            createList(cityName);
            populateGeofenceList();
            addGeofences();
            if(!cityName.equals("") && cityName != null)
            {
                //city_text.setText(cityName);
                Intent intent = new Intent(this,PlaceChooserActivity.class);
                intent.putExtra("city",cityName );
                intent.putExtra("user", userDTO);
                startActivity(intent);
            }
            else
            {
                city_text.setText("City could not be found!");
            }
        }
        else
        {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.commit();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {

        for (PlaceDTO entry : ((PlaceDTOArray) this.getApplication()).getPlaces()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getName())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getLat(),
                            entry.getLon(),
                            entry.getRadius()
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }

        /*
        for (Map.Entry<String, LatLng> entry : Constants.DUNDALK.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }
        **/
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    protected String getCity()
    {
        List<Address> addresses = null;

        String returnVal = "";

        try {
            addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, "Service not available: ", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, "Invalid coordinates used: " + ". " +
                    "Latitude = " + mLastLocation.getLatitude() +
                    ", Longitude = " +
                    mLastLocation.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0)
        {
                Log.e(TAG, "Address was not found!");
        }
        else
        {
            Address address = addresses.get(0);
            int cityIndex = address.getMaxAddressLineIndex() - 2;
            returnVal = address.getAddressLine(cityIndex);
            ((PlaceDTOArray)getApplication()).setCity(returnVal);
        }

        return returnVal;
    }

    public void test(View view)
    {
        Intent intent = new Intent(this,PlaceChooserActivity.class);
        intent.putExtra("city",cityName );
        startActivity(intent);
    }

    protected void onPause()
    {
        super.onPause();
        //progress.dismiss();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        progress.dismiss();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    public void createList(String cityName)
    {

        if (!cityName.equals(""))
        {
            if(!((PlaceDTOArray)this.getApplication()).getPlaces().isEmpty())
            {
                ((PlaceDTOArray)this.getApplication()).clear();
                ((PlaceDTOArray)this.getApplication()).clearImages();
            }
            XMLParser parser = new XMLParser(cityName.toLowerCase());
            String xml = parser.getXmlFromFile(LocationActivity.this); // getting XML
            if(xml.equals("CITY NOT FOUND OR NOT SUPPORTED YET"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setTitle("Unsupported location");
                builder.setMessage("CITY NOT FOUND OR NOT SUPPORTED");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(LocationActivity.this , LocationActivity.class);
                        startActivity(intent);
                    }
                });

            }
            Document doc = parser.getDomElement(xml); // getting DOM element

            NodeList nl = doc.getElementsByTagName(KEY_PLACE);

            for (int i = 0; i < nl.getLength(); i++)
            {
                Element e = (Element) nl.item(i);

                String name = parser.getValue(e, KEY_NAME);
                String icon = parser.getValue(e, KEY_ICON);
                String time = parser.getValue(e, KEY_TIME);
                String latString = parser.getValue(e, KEY_LAT);
                double lat = Double.parseDouble(latString);
                String lonString = parser.getValue(e, KEY_LON);
                double lon = Double.parseDouble(lonString);
                String radiusString = parser.getValue(e, KEY_RADIUS);
                float radius = Float.parseFloat(radiusString);

                int iconId = getResources().getIdentifier(icon , "drawable", this.getPackageName());
                PlaceDTO p = new PlaceDTO(iconId, name, time, cityName, lat, lon, radius);


                ((PlaceDTOArray)this.getApplication()).addPlace(p);
            }

        }
        else
        {
            Toast.makeText(this, "No Data for this city yet", Toast.LENGTH_LONG);
        }
    }

}


