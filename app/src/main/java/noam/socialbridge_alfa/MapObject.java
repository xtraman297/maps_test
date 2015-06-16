package noam.socialbridge_alfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
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
    protected String strUserName;
    protected String strUserEmail;
    public Marker markUserMarker;
    protected Pubnub pubStreamer;
    protected Context connectedContext;
    protected LatLng locUpdatedLocation;
    protected LocationThread thrThread;

    // Constructors



    /**
     * The default constructor for: MapObject{@link noam.socialbridge_alfa.MapObject}
     * {@link noam.socialbridge_alfa.PersonMapObject} and
     * {@link noam.socialbridge_alfa.UserMapObject} classes.
     * This is the base constructor, all other classes object are being created through here.
     * @param strUserName       - The user name (nickname)
     * @param ltlngUserLocation - {@link LatLng} The initial location of the user
     * @param strUserEmail      - The user's email address
     * @param connectedContext  - {@link android.content.Context} Needed context to get resources
     */
    public MapObject(String strUserName,
                     String strUserEmail,
                     LatLng ltlngUserLocation,
                     final Context connectedContext){
        this.thrThread = new LocationThread(this, strUserName, ltlngUserLocation);
        this.strUserEmail = strUserEmail;
        this.strUserName = strUserName;
        this.connectedContext = connectedContext;

        // Initialize pubnub dataMember with publish and subscribe keys
        try {
            ApplicationInfo aiMetaData = this.connectedContext.getPackageManager()
                    .getApplicationInfo(this.connectedContext.getPackageName(),
                                        PackageManager.GET_META_DATA);
            this.pubStreamer =
                new Pubnub(aiMetaData.metaData.get("pubnubAPI_publish").toString(),
                           aiMetaData.metaData.get("pubnubAPI_subscribe").toString());
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
            int my_image_id = R.drawable.usersample;
            if (strUserEmail.equals("test1@gmail.com")){
                my_image_id = R.drawable.moshe;
            }
            if (strUserEmail.equals("test2@gmail.com")){
                my_image_id = R.drawable.noam;
            }
            if (strUserEmail.equals("test3@gmail.com")){
                my_image_id = R.drawable.nadia;
            }
            if (strUserEmail.equals("test4@gmail.com")){
                my_image_id = R.drawable.daniel;
            }
            Bitmap userImage = BitmapFactory.decodeResource(connectedContext.getResources(),
                                                            my_image_id);
            Bitmap markerImage = BitmapFactory.decodeResource(connectedContext.getResources(),
                                                              R.drawable.usersample);
            Bitmap myImage = ReturnMarkerWithImage.ReturnBitmap(userImage, markerImage);
            Bitmap titleImage = ReturnMarkerWithImage.drawTextToBitmap(connectedContext,
                                                                       myImage,
                                                                       strUserName);
            this.markUserMarker = MapsActivity.mMap
                    .addMarker(new MarkerOptions()
                            .position(ltlngUserLocation)
                            .title(strUserName)
                            .icon(BitmapDescriptorFactory.fromBitmap(titleImage)));
        }
    }

    /**
     * Abstract method for running actions on the map through the Main Thread
     * (because android interface prohibits running action on maps through other the main threads,
     * such as pubnub).
     */
    @Override
    public abstract void run();

    /**
     * Callback method for logging connectivity from pubnub
     * @param channel   - The channel of the message
     * @param response  - The given info
     */
    public void successCallback(String channel, Object response) {
        System.out.println(response.toString());
    }

    /**
     * Callback method for logging connectivity from pubnub
     * @param channel   - The channel of the message
     * @param error     - Description of the error that happened
     */
    public void errorCallback(String channel, PubnubError error) {
        System.out.println(error.toString());
    }

    /**
     * Method for updating the user's position on the main map.
     * It sets the user's marker position by calling the main thread with {@code #runOnUiThread}
     * @param newLocation   - The new location to update.
     */
    public void updatePosition(LatLng newLocation) {
        // Permit policy for all the threads
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.locUpdatedLocation = newLocation;

        /**
         * Run this action on main thread because android does not allow map manipulations
         * from other threads than the main.
         */
        ((Activity)this.connectedContext).runOnUiThread(this);
    }

    // Getters and Setters
    public String getStrUserName() {
        return strUserName;
    }
}
