package noam.socialbridge_alfa;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private AlertPubSend alertSend;

    public PersonMapObject(String strUserName, LatLng latlngUserLocation, Context context) {
        super(strUserName, latlngUserLocation, context);

        try {
            this.pubStreamer.subscribe("positionChange", this);
        }
        catch (PubnubException pe) {
            pe.printStackTrace();
            System.out.println(pe.getPubnubError().toString());
        }

        this.alertSend = new AlertPubSend(context,this.strUserEmail + "-" + "chat", this.markUserMarker);
    }

    /**
     * This method handles all the pubnub publish request AS THE MAIN THREAD !
     */
    @Override
    public void run() {
    }

    /**
     * Method to test callback form pubnub
     * @param channel - channel name
     * @param message - the content of the message
     */
    @Override
    public void connectCallback(String channel, Object message) {
        //super.connectCallback(channel, message);
        int x =1;
    }

    /**
     * This method is activated when the callback from a pubnub publish is successful.
     * It suppose to update the user's position (calling the updatePosition in the super class).
     * @param channel - channel name
     * @param message - the content of the message
     */
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

    /**
     * This method is called only when there are publishing errors
     * @param channel - channel name
     * @param error - the content of the error message
     */
    @Override
    public void errorCallback(String channel, PubnubError error) {
        System.out.println("SUBSCRIBE : ERROR on channel " + channel
                + " : " + error.toString());
    }

}
