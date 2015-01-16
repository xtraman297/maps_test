package noam.socialbridge_alfa;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.location

public class MapsActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraChangeListener{

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private CameraPosition cuMyInitPos;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient clGoogleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //ConnectToLocationServices();
        clGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for resolution when there are connection error
        if (!this.mResolvingError) {
            this.clGoogleClient.connect();
        }
    }

    @Override
    protected void onStop() {
        this.clGoogleClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if the connection error is that resulted
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            this.mResolvingError = false;

            if (resultCode == RESULT_OK) {
                // Avoid when the app is connecting
                if (!this.clGoogleClient.isConnecting() &&
                        !this.clGoogleClient.isConnected()) {
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
        this.mMap.addMarker(new MarkerOptions().position(getDeviceLocation()).title("MyPos"));
        LatLng Moshe_Test = new LatLng(getDeviceLocation().latitude + 0.002,getDeviceLocation().longitude + 0.002);
        this.mMap.addMarker(new MarkerOptions().position(getDeviceLocation())
                .title("MyPos")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.noam)));

        mMap.addMarker(new MarkerOptions().position(Moshe_Test).title("Fuck"));
        this.cuMyInitPos = new CameraPosition.Builder().target(getDeviceLocation())
                .zoom(15.5f)
                .build();
        this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cuMyInitPos));

        //this creates the location manager and listener
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
        mMap.addCircle(new CircleOptions()
                .center(getDeviceLocation())
                .radius(400)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(30,60,50,40)));
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
        if (this.mResolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                this.mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            // This may be with errors
            catch (IntentSender.SendIntentException exception) {
                // Try again to connect
                this.clGoogleClient.connect();
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
//                    .newInstance(new GoogleMapOptions().zoomGesturesEnabled(false)).getMap();
                    .getMap();

            // Add more map options
            mMap.getUiSettings().setZoomGesturesEnabled(false);

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
        if (this.clGoogleClient.isConnected()) {
            mMap.addMarker(new MarkerOptions().position(getDeviceLocation()).title("My1Pos"));
        }
    }

    private LatLng getDeviceLocation() {
        return new LatLng(
                (LocationServices.FusedLocationApi.getLastLocation(this.clGoogleClient)).getLatitude(),
                (LocationServices.FusedLocationApi.getLastLocation(this.clGoogleClient)).getLongitude());
    }

    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //here we should delete the older markers
            //This will happen for every change
            mMap.addMarker(new MarkerOptions().position(getDeviceLocation()).title("MyPos2"));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }
    };
}
