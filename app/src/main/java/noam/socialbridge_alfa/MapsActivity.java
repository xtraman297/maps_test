package noam.socialbridge_alfa;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.location.LocationManager;
import android.location.Location;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.util.Hashtable;

public class MapsActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraChangeListener{

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static GoogleApiClient clGoogleClient;

    private Hashtable<String, MapObject> moObjects = new Hashtable<>();
    String MyEmail = "test1@gmail.com";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        clGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.MyEmail = getIntent().getExtras().getString("email");
        setUpMapIfNeeded();
        setUpMapObjects();


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for resolution when there are connection error
        if (!this.mResolvingError) {
            MapsActivity.clGoogleClient.connect();
        }
    }

    @Override
    protected void onStop() {
        MapsActivity.clGoogleClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if the connection error is that resulted
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            this.mResolvingError = false;

            if (resultCode == RESULT_OK) {
                // Avoid when the app is connecting
                if (!MapsActivity.clGoogleClient.isConnecting() &&
                        !MapsActivity.clGoogleClient.isConnected()) {
                    clGoogleClient.connect();
                }
            }
        }
    }

    /**
     * Happens when clGoogleApiClient is connected successfully,
     * Here we will implement and call all the operations regarding the API client
     * @param bundle -
     */
    @Override
    public void onConnected(Bundle bundle) {
        setUpMapIfNeeded();
        CameraPosition cuMyInitPos = new CameraPosition.Builder().target(getDeviceLocation())
                .zoom(15.5f)
                .build();
        MapsActivity.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cuMyInitPos));
        //this creates the location manager and listener
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                  5,
                                  5,
                                  (UserMapObject.getUserObject(this, this.MyEmail)));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.

        // Kick out if there is resolution currently going
       if (result.hasResolution()) {
            try {
                this.mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            // This may be with errors
            catch (IntentSender.SendIntentException exception) {
                // Try again to connect
                MapsActivity.clGoogleClient.connect();
            }
        }
        // Resolution failed
        else {
            Log.e("NoamTesting", "Resolution and connection failed at MapsAcivity");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets, and puts all map objects that are given from the sever
     */
    private void setUpMapObjects() {
        moObjects.clear();
        JSONArray jsonAllUsers = SocialBridgeActionsAPI.GetRequest("user", this);

        // Iterate over all the users from the server and add them to the hash table
        for (int nUser = 0; nUser < jsonAllUsers.length(); nUser++) {
            try {
                JSONObject joCurr = ((JSONObject)jsonAllUsers.get(nUser));

                moObjects.put(joCurr.get("user_name").toString(),
                              new PersonMapObject(joCurr.get("email").toString(),
                                                  new LatLng((double)((JSONObject)joCurr
                                                                .get("location")).get("latitude"),
                                                             (double)((JSONObject)joCurr
                                                                .get("location")).get("longitude")),
                              this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        moObjects.put("me", UserMapObject.getUserObject(this, this.MyEmail));
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Add more map options
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            // Add more basic attributes to the main map
//            mMap
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * A method to contain the camera zoom level at maximum and minimum
     * TODO:implement later
     * @param position - Camera position
     */
    @Override
    public void onCameraChange(CameraPosition position) {

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Act only if connected to Maps and location API
        if (MapsActivity.clGoogleClient.isConnected()) {
            //mMap.addMarker(new MarkerOptions().position(getDeviceLocation()).title("My1Pos"));
        }
    }

    public static LatLng getDeviceLocation() {
        Location locCurrentLocation = (LocationServices
                                        .FusedLocationApi
                                        .getLastLocation(MapsActivity.clGoogleClient));
        if(locCurrentLocation != null) {
            return new LatLng(locCurrentLocation.getLatitude(), locCurrentLocation.getLongitude());
        }
        else {
            return new LatLng(0,0);
        }
    }
    public static void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
