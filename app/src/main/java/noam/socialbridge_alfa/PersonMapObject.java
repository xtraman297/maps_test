package noam.socialbridge_alfa;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MrJellyB on 13/03/2015.
 * A class that represent all the people in the map except the user himself.
 * For each person there will be a thread (its inherited from class {@link MapObject})
 */
public class PersonMapObject extends MapObject {
    public PersonMapObject(String strUserName, LatLng ltlngUserLocation, Context context) {
        super(strUserName, ltlngUserLocation, context);
        //this.thrThread.start();
        try {
            this.pubStreamer.subscribe("positionChange", this);
        }
        catch (PubnubException pe) {
            pe.printStackTrace();
            System.out.println(pe.getPubnubError().toString());
        }
    }

    @Override
    public void run() {
        // Only update position if they are different
        if ((this.locUpdatedLocation.longitude != this.markUserMarker.getPosition().longitude) ||
                (this.locUpdatedLocation.latitude != this.markUserMarker.getPosition().latitude)) {
            this.markUserMarker.setPosition(this.locUpdatedLocation);
        }
    }

    @Override
    public void connectCallback(String channel, Object message) {
        //super.connectCallback(channel, message);
        int x =1;
    }

    @Override
     public void successCallback(String channel, Object message) {
        // Cast the response
        JSONObject joResponse = (JSONObject) message;

        try {
            this.updatePosition(new LatLng(
             (Double.parseDouble(((JSONObject)joResponse.get("location_attributes")).get("latitude").toString())),
             (Double.parseDouble(((JSONObject)joResponse.get("location_attributes")).get("longitude").toString()))));
        }
        catch (JSONException je) {

        }
    }

    @Override
    public void errorCallback(String channel, PubnubError error) {
        System.out.println("SUBSCRIBE : ERROR on channel " + channel
                + " : " + error.toString());
    }
}
