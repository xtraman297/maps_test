package noam.socialbridge_alfa;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;

import com.pubnub.api.*;
import org.json.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.Callable;

/**
 * Created by noam2_000 on 06/03/2015.
 * A base class for {@link noam.socialbridge_alfa.UserMapObject,
 *                   @link noam.socialbridge_alfa.PersonMapObject}
 * that contains the thread and basic info of the person
 */
public abstract class MapObject extends Callback implements Runnable {
    // User protected members
    protected String strUserEmail;
    protected Marker markUserMarker;
    protected Pubnub pubStreamer;
    protected Context connectedContext;

    protected LocationThread thrThread;

    public MapObject(String strUserName, LatLng ltlngUserLocation, Context connectedContext){
        this.thrThread = new LocationThread(this, strUserName, ltlngUserLocation);
        this.strUserEmail = strUserName;
        this.connectedContext = connectedContext;

        // Initialize pubnub dataMember with publish and subscribe keys
        try {
            ApplicationInfo aiMetaData = this.connectedContext.getPackageManager()
                    .getApplicationInfo(this.connectedContext.getPackageName(),
                                        PackageManager.GET_META_DATA);
            this.pubStreamer =
                new Pubnub(aiMetaData.metaData.get("pubnubAPI_publish").toString(),
                           aiMetaData.metaData.get("pubnubAPI_subscribe").toString());
            //this.pubStreamer.time(this);

            try {
                this.pubStreamer.subscribe(this.strUserEmail, this);
//                this.pubStreamer.publish("test",
//                        new JSONObject(String.format("{\"latitude\":%f,\"longitude\":%f}", 10.0, 10.0)),
//                        this);
            }
            catch (PubnubException pe) {
                pe.printStackTrace();
            }
//                catch (JSONException e) {
//                e.printStackTrace();
//            }
//            catch(JSONException je) {
//                je.printStackTrace();
//            }

        }
        catch (PackageManager.NameNotFoundException exception) {
            System.out.println("MapObject.MapObject " + exception.getMessage());
            exception.printStackTrace();
        }

        // Add the marker to the main map if its not null
        if (MapsActivity.mMap != null) {
            this.markUserMarker = MapsActivity.mMap
                    .addMarker(new MarkerOptions().position(ltlngUserLocation));
        }
    }

    @Override
    public abstract void run();

    public void successCallback(String channel, Object response) {
        System.out.println(response.toString());
    }

    public void errorCallback(String channel, PubnubError error) {
        System.out.println(error.toString());
    }

    public void updatePosition(LatLng newLocation) {
        // Only update position if they are different
        if ((newLocation.longitude != this.markUserMarker.getPosition().longitude) ||
                (newLocation.latitude != this.markUserMarker.getPosition().latitude)) {
            this.markUserMarker.setPosition(newLocation);
        }
    }
}
