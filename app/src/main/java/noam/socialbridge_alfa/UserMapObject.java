package noam.socialbridge_alfa;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.StrictMode;
import android.os.Bundle;
import android.location.LocationListener;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by MrJellyB on 13/03/2015.
 * A singleton class that should return always one instance of user object
 */
public class UserMapObject extends MapObject implements LocationListener {
    private static UserMapObject umoUser = null;
    private AlertPubGet alertGet;

    /**
     * Private constructor for building singleton object for the current user on the map
     * @param connectedContext  - {@link android.content.Context} For getting activity resources
     * @param strMyEmail        - String that we use to save user's email
     */
    private UserMapObject(Context connectedContext, String strMyEmail, String strMyUserName) {
        super(strMyUserName, strMyEmail, MapsActivity.getDeviceLocation(), connectedContext);

        this.alertGet = new AlertPubGet(this.connectedContext, this.strUserName + "-chat");
    }

    /**
     * Implement singleton principals. First time build and all the other calls get the same user
     * @param context       - {@link android.content.Context} For getting resources from the activity
     * @param strMyEmail    - String that we use to save user's email
     * @return              - Return the user.
     */
    public static UserMapObject getUserObject(Context context) throws Exception{
        // Dont continue if the right globals are not initialized
        if (Globals.UserEmail == null || Globals.UserName == null) {
            throw (new Exception("Globals is not initiated"));
        }

        if (UserMapObject.umoUser == null) {
            UserMapObject.umoUser = new UserMapObject(context, Globals.UserEmail, Globals.UserName);
        }

        return (UserMapObject.umoUser);
    }

    @Override
    public void run() {
        // Only update position if they are different
    }

    /**
     * Update the current user's location
     * @param newLocation   - The new location to update.
     */
    @Override
    public void updatePosition(LatLng newLocation) {
        // Send to super class for update the position on the map
        super.updatePosition(newLocation);

        SocialBridgeActionsAPI.updateUserLocation(this.strUserEmail,
                this.markUserMarker.getPosition(),
                this.connectedContext);

        CameraPosition cuMyPos = new CameraPosition.Builder()
                .target(newLocation)
                .zoom(15.5f)
                .build();
        MapsActivity.animateMarker(this.markUserMarker, newLocation, false);
    }

    /**
     * Listen for location changes, update location and send pubnub publish
     * @param location - The location that changed
     */
    @Override
    public void onLocationChanged(final Location location) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Update my position
        updatePosition(new LatLng(location.getLatitude(),location.getLongitude()));
        System.out.println(location.getLatitude());
        try {
            // Publish new location to other users
            JSONObject joNewLoc = new JSONObject(String.format("{location_attributes:" +
                                                                    "{longitude:%f, latitude:%f}}",
                                                location.getLongitude(),
                                                location.getLatitude()));
            this.pubStreamer.publish(this.strUserEmail, joNewLoc, this);
        }
        catch (JSONException je) {
            je.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
