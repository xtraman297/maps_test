package noam.socialbridge_alfa;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Hashtable;
//import com.google.android.gms.location

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
        //this.mMap.addMarker(new MarkerOptions().position(getDeviceLocation()).title("MyPos"));
        //LatLng Moshe_Test = new LatLng(getDeviceLocation().latitude + 0.002,getDeviceLocation().longitude + 0.002);
        //this.mMap.addMarker(new MarkerOptions().position(getDeviceLocation())
        //        .title("MyPos"));
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.noam)));

        //mMap.addMarker(new MarkerOptions().position(Moshe_Test).title("Fuck"));
        CameraPosition cuMyInitPos = new CameraPosition.Builder().target(getDeviceLocation())
                .zoom(15.5f)
                .build();
        MapsActivity.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cuMyInitPos));

        //this creates the location manager and listener
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                  5,
                                  5,
                                  (UserMapObject.getUserObject(this)));
        //mMap.addCircle(new CircleOptions()
        //        .center(getDeviceLocation())
        //        .radius(4)
        //        .strokeColor(Color.RED)
        //        .fillColor(Color.argb(30,60,50,40)));
        //check_location();
        //GET("http://104.155.7.53/user");
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

                moObjects.put(joCurr.get("name").toString(),
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

        moObjects.put("me", UserMapObject.getUserObject(this));
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
        //return new LatLng(10.0,20.0);
    }

    public final LocationListener locationListener = new LocationListener() {
        JSONArray all_users_from_get = new JSONArray();
        String user_name = "not_found_yet";

        @Override
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @SuppressLint("NewApi")
        public void onLocationChanged(final Location location) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //here we should delete the older markers
            //This will happen for every change of GPS
            String user_email = "test1@gmail.com";
            String my_url_get_post = "http://104.155.7.53/user";
            int user_id = -1;

            user_name = FindMyUserName(all_users_from_get, user_email);
            user_id = FindMyID(all_users_from_get, user_email);

            String my_url_put = String.format("%s/%d", my_url_get_post, user_id);


            String entity_format_put = String.format("{\"user\":{\"name\":\"%s\",\"location_attributes\":{\"latitude\":%f,\"longitude\":%f}}}", user_name, getDeviceLocation().latitude, getDeviceLocation().longitude);
            StringEntity entity_for_put = null;
            try {
                entity_for_put = new StringEntity(entity_format_put);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            SendPutUpdate(my_url_put, entity_for_put);
            //all_users_from_get = SocialBridgeActionsAPI.GetRequest("user",(Context)this);

            mMap.clear();

            //AddMarkersFromJSON(all_users_from_get);
            //mMap.addMarker(new MarkerOptions().position(getDeviceLocation()).title(user_name));
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

    public JSONArray GetRequest(String myurl) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(myurl);
        HttpResponse response;
        JSONArray json;
        json = null;
        try {
            response = client.execute(request);
            System.out.println(response.getStatusLine());
            String responseText = null;
            responseText = EntityUtils.toString(response.getEntity());
            System.out.println(responseText);
            json = null;
            try {
                json = new JSONArray(responseText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }
    public void AddMarkersFromJSON(JSONArray json_arr){
        System.out.println(json_arr);
        System.out.println(json_arr.length());
        JSONObject temp_in_for = null;
        String temp_name = "name_not_found";
        //LatLng temp_users = new LatLng(0.0, 0.0);
        for (int i = 0; json_arr.length() != i; i++){
            try {

                System.out.println(json_arr.getString(i));

                temp_in_for = new JSONObject(json_arr.getString(i));
                System.out.println(temp_in_for.getString("name"));
                temp_name = temp_in_for.getString("name");
                System.out.println(temp_in_for.getString("email"));
                temp_in_for = temp_in_for.getJSONObject("location");
                System.out.println(temp_in_for.getString("latitude"));
                System.out.println(temp_in_for.getString("longitude"));
                /*temp_users.latitude = temp_in_for.getDouble("latitude");
                temp_users.longitude = temp_in_for.getDouble("longitude");*/
                //Marker test = mMap.addMarker(new MarkerOptions().position(new LatLng(temp_in_for.getDouble("latitude"), temp_in_for.getDouble("longitude"))).title(temp_name  ));
                mMap.addMarker(new MarkerOptions().position(new LatLng(temp_in_for.getDouble("latitude"), temp_in_for.getDouble("longitude"))).title("one_test").icon(BitmapDescriptorFactory.fromBitmap(ReturnMarkerWithImage.ReturnBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.noam),BitmapFactory.decodeResource(getResources(), R.drawable.usersample)))));
                //LatLng test_lat = new LatLng(temp_in_for.getDouble("latitude") + 10, temp_in_for.getDouble("longitude") + 10);
                //animateMarker(test, test_lat, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }}

    }
    public void SendPutUpdate(String myurl, StringEntity params){
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPut request = new HttpPut(myurl);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseText = null;
            responseText = EntityUtils.toString(response.getEntity());
            System.out.println(myurl);
            System.out.println(responseText);
            System.out.println(request.getEntity());
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public int FindMyID(JSONArray json_arr, String my_email){
        JSONObject temp_in_for = null;
        for (int i = 0; json_arr.length() != i; i++)
            try {
                temp_in_for = new JSONObject(json_arr.getString(i));
                if (my_email.equals(temp_in_for.getString("email"))) {
                    temp_in_for = temp_in_for.getJSONObject("location");
                    return temp_in_for.getInt("user_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return -1;
    }
    public String FindMyUserName(JSONArray json_arr, String my_email){
        JSONObject temp_in_for = null;
        for (int i = 0; json_arr.length() != i; i++)
            try {
                temp_in_for = new JSONObject(json_arr.getString(i));
                if (my_email.equals(temp_in_for.getString("email"))) {
                    return temp_in_for.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return "not_found_yet";
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
    public void CheckForUpdates(){
        System.out.println("Check Updates from server compared to local configs");
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void thread_runner()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mMap.clear();
        new Thread(new Runnable() {
            public void run() {
                String user_email = "test21@gmail.com";
                JSONArray all_users_from_get = new JSONArray();
                String user_name = "not_found_yet";
                user_name = FindMyUserName(all_users_from_get, user_email);
                int user_id = -1;
                String my_url_get_post = "http://104.155.7.53/user";
                int fail_count = 0;
                String entity_format_put = String.format("{\"user\":{\"name\":\"%s\",\"location_attributes\":{\"latitude\":%f,\"longitude\":%f}}}", user_name, getDeviceLocation().latitude, getDeviceLocation().longitude);
                StringEntity entity_for_put = null;
                user_id = FindMyID(all_users_from_get, user_email);
                String my_url_put = String.format("%s/%d", my_url_get_post, user_id);
                try {
                    entity_for_put = new StringEntity(entity_format_put);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                while (true) {
                    SendPutUpdate(my_url_put, entity_for_put);
                    CheckForUpdates();
                    try {
                        if (fail_count >= 5) {
                            all_users_from_get = GetRequest(my_url_get_post);
                            mMap.clear();
                            //AddMarkersFromJSON(all_users_from_get);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
