package noam.socialbridge_alfa;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by MrJellyB on 14/03/2015.
 */
public class LocationThread extends Thread {
    private LatLng lngLocation;

    public LocationThread(Runnable runnable, String threadName, LatLng lngLocation) {
        super(runnable, threadName);
        this.lngLocation = lngLocation;
    }

    public void setLngLocation(LatLng lngLocation) {
        this.lngLocation = lngLocation;
    }

    public LatLng getLngLocation() {
        return lngLocation;
    }
}
