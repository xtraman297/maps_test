package noam.socialbridge_alfa;

import android.os.StrictMode;
import android.os.Bundle;
import android.location.LocationListener;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
/**
 * Created by MrJellyB on 13/03/2015.
 * A singleton class that should return always one instance of user object
 */
public class UserMapObject extends MapObject implements LocationListener {
    private static UserMapObject umoUser = null;

    private UserMapObject() {
        super("Me", MapsActivity.getDeviceLocation());
    }

    public static UserMapObject getUserObject() {
        if (UserMapObject.umoUser == null) {
            UserMapObject.umoUser = new UserMapObject();
        }

        return (UserMapObject.umoUser);
    }

    @Override
    public void run() {
        //JSONArray jsonAllUsers = SocialBridgeActionsAPI.GetRequest("user", this);
    }

    @Override
    public void updatePosition(LatLng newLocation) {
        super.updatePosition(newLocation);
        //SocialBridgeActionsAPI.SendPutUpdate("user/update/" + this.strUserName, )
    }

    @Override
    public void onLocationChanged(final Location location) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        updatePosition(new LatLng(location.getLatitude(),location.getLongitude()));
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
