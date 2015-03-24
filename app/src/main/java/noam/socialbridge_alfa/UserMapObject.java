package noam.socialbridge_alfa;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.os.Bundle;
import android.location.LocationListener;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by MrJellyB on 13/03/2015.
 * A singleton class that should return always one instance of user object
 */
public class UserMapObject extends MapObject implements LocationListener {
    private static UserMapObject umoUser = null;

    private UserMapObject(Context connectedContext) {
        super("test1@gmail.com", MapsActivity.getDeviceLocation(), connectedContext);
    }

    public static UserMapObject getUserObject(Context context) {
        if (UserMapObject.umoUser == null) {
            UserMapObject.umoUser = new UserMapObject(context);
        }

        return (UserMapObject.umoUser);
    }

    @Override
    public void run() {
        // Only update position if they are different
    }

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
        //MapsActivity.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cuMyPos));
        MapsActivity.animateMarker(this.markUserMarker, newLocation, false);

        //this.markUserMarker.setPosition(newLocation);
    }

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
