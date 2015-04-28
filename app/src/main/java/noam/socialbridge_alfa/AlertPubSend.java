package noam.socialbridge_alfa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.widget.EditText;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.pubnub.api.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MrJellyB on 28/04/2015.
 */
public class AlertPubSend extends AlertPub
        implements GoogleMap.OnMarkerClickListener{

    public EditText input;
    private Marker myMarker;

    public AlertPubSend(Context connectedContext, String strChannel, Marker myMarker) {
        super(connectedContext, strChannel);
        this.myMarker = myMarker;

        if (MapsActivity.mMap != null) {
            MapsActivity.mMap.setOnMarkerClickListener(this);
        }
    }

    @Override
    public boolean onMarkerClick( Marker marker ) {
        this.strChannel = marker.getTitle() + "-chat";
        System.out.println("aaaaaaaaaaa");
        this.build_and_run_alert();

        // TODO: Interaction Menu should be here?
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Exit method if input is null
        if (this.input == null) {
            return;
        }

        Editable value = this.input.getText();
        this.pubnub.publish(this.strChannel, value.toString(), this);
    }

    public void build_and_run_alert(){
        // Raise Exception if connectedContext is null
        if(this.connectedContext == null) {
            throw new NullPointerException();
        }

        this.alert = new AlertDialog.Builder(this.connectedContext);
        this.input = new EditText(connectedContext);
        this.alert.setTitle("Title")
                .setMessage("Message")
                .setView(input)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        // Why do we crash here?
                        System.out.println("I shouldn't crash here but i do.. ?!");
                    }
                });

        this.alert.setPositiveButton("Ok", this);
        this.alert.show();
    }
}
