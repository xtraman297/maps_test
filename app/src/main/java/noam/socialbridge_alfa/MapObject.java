package noam.socialbridge_alfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    protected LatLng locUpdatedLocation;
    protected LocationThread thrThread;

    /**
     * The default constructor for: MapObject{@link noam.socialbridge_alfa.MapObject}
     * {@link noam.socialbridge_alfa.PersonMapObject} and
     * {@link noam.socialbridge_alfa.UserMapObject} classes.
     * This is the base constructor, all other classes object are being created through here.
     */
    public MapObject(String strUserName, LatLng ltlngUserLocation, final Context connectedContext){
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
            }
            catch (PubnubException pe) {
                pe.printStackTrace();
            }

        }
        catch (PackageManager.NameNotFoundException exception) {
            System.out.println("MapObject.MapObject " + exception.getMessage());
            exception.printStackTrace();
        }

        // Add the marker to the main map if its not null
        if (MapsActivity.mMap != null) {
            //MapsActivity.mMap.setOnMapClickListener(this.markUserMarker);

            Bitmap userImage = BitmapFactory.decodeResource(connectedContext.getResources(), R.drawable.noam);
            Bitmap markerImage = BitmapFactory.decodeResource(connectedContext.getResources(), R.drawable.usersample);
            Bitmap myImage = ReturnMarkerWithImage.ReturnBitmap(userImage, markerImage);
            Bitmap titleImage = ReturnMarkerWithImage.drawTextToBitmap(connectedContext, myImage, strUserName);
            this.markUserMarker = MapsActivity.mMap
                    .addMarker(new MarkerOptions()
                            .position(ltlngUserLocation)
                            .title(strUserName)
                            .icon(BitmapDescriptorFactory.fromBitmap(titleImage)));
            //this.markUserMarker.showInfoWindow();
//            MapsActivity.mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick( Marker marker ) {
//                    //Noamisking
//                    System.out.println("aaaaaaaaaaa");
//                    //build_and_run_alert();
//                    return true;
//
//                }
//            });
            //this.markUserMarker = MapsActivity.mMap
            //        .addMarker(new MarkerOptions().position(ltlngUserLocation).title(strUserName));
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

    /**
     * Method for updating the user's position on the main map.
     * It sets the user's marker position by calling the main thread with {@code #runOnUiThread}
     * @param newLocation  The new location to update.
     */
    public void updatePosition(LatLng newLocation) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        // Only update position if they are different
//        if ((newLocation.longitude != this.markUserMarker.getPosition().longitude) ||
//                (newLocation.latitude != this.markUserMarker.getPosition().latitude)) {
//            this.markUserMarker.setPosition(newLocation);
//        }
        this.locUpdatedLocation = newLocation;
        ((Activity)this.connectedContext).runOnUiThread(this);
    }
}
