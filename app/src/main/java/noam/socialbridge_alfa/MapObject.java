package noam.socialbridge_alfa;

import android.support.v4.app.FragmentActivity;

//import com.pubnub.api.*;
//import org.json.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by noam2_000 on 06/03/2015.
 * A base class for {@link noam.socialbridge_alfa.UserMapObject,
 *                   @link noam.socialbridge_alfa.PersonMapObject}
 * that contains the thread and basic info of the person
 */
public abstract class MapObject extends FragmentActivity implements Runnable {
    // User protected members
    protected String strUserName;
    protected Marker markUserMarker;
//    protected Pubnub pubStreamer;

    protected Thread thrThread;

    public MapObject(String strUserName, LatLng ltlngUserLocation){
        this.thrThread = new Thread(this, strUserName);
        this.strUserName = strUserName;

        // Add the marker to the main map if its not null
        if (MapsActivity.mMap != null) {
            this.markUserMarker = MapsActivity.mMap
                    .addMarker(new MarkerOptions().position(ltlngUserLocation));
        }
    }

    public MapObject(String ThreadName){
        this.thrThread = new Thread(this, ThreadName);
    }

    @Override
    public abstract void run();

    public void updatePosition(LatLng newLocation) {
        // Only update position if they are different
        if ((newLocation.longitude != this.markUserMarker.getPosition().longitude) ||
                (newLocation.latitude != this.markUserMarker.getPosition().latitude)) {
            this.markUserMarker.setPosition(newLocation);
        }
    }

    public void startObjectThread (){
        if (this.thrThread == null){
            this.thrThread = new Thread(this, "test");
        }

        this.thrThread.start();
    }
}
