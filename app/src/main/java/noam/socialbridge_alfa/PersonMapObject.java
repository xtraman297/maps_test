package noam.socialbridge_alfa;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by MrJellyB on 13/03/2015.
 * A class that represent all the people in the map except the user himself.
 * For each person there will be a thread (its inherited from class {@link MapObject})
 */
public class PersonMapObject extends MapObject {
    public PersonMapObject(String strUserName, LatLng ltlngUserLocation) {
        super(strUserName, ltlngUserLocation);
        this.thrThread.start();
    }

    @Override
    public void run() {
        // Update the marker of this person

    }
}
