package noam.socialbridge_alfa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.pubnub.api.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MrJellyB on 28/04/2015.
 * This class is used to send messages to chat with pubnub interface.
 * It listens to the markers on the map and trigger the alert message
 */
public class AlertPubSend extends AlertPub
        implements GoogleMap.OnMarkerClickListener{

    public EditText input;
    public String UserEmail;

    /**
     * This constructor builds the alert object for publishing messages
     * @param connectedContext - {@link android.content.Context} Context for accessing resources
     * @param strChannel       - The pubnub channel to publish from
     */
    public AlertPubSend(Context connectedContext, String strChannel, Marker myMarker) {
        super(connectedContext, strChannel);

        if (MapsActivity.mMap != null) {
            MapsActivity.mMap.setOnMarkerClickListener(this);
        }
    }

    /**
     * The marker click handler
     * @param marker - {@link com.google.android.gms.maps.model.Marker} The associated marker
     * @return       - A boolean to continue or stop the flow
     */
    @Override
    public boolean onMarkerClick( Marker marker ) {
        this.strChannel = marker.getTitle() + "-chat";
        this.UserEmail = marker.getTitle();
        //this.build_and_run_alert();
        this.showUserPopup();
        // TODO: Interaction Menu should be here?
        return true;
    }

    /**
     * This method is used for publishing the messages
     * @param dialog - {@link DialogInterface} the dialog object
     * @param which  - which button was clicked
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Exit method if input is null
        if (this.input == null) {
            return;
        }

        Editable value = this.input.getText();
        this.pubnub.publish(this.strChannel, value.toString(), this);
    }

    /**
     * Show actions menu for the marker's user
     */
    public void showUserPopup(){

        // create a Dialog component
        final Dialog dialog = new Dialog(this.connectedContext);

        //tell the Dialog to use the userview.xml as it's layout description
        dialog.setContentView(R.layout.userview);
        dialog.setTitle(this.UserEmail);

        ImageView img = (ImageView) dialog.findViewById(R.id.imageView);
        int my_image_id = R.drawable.usersample;
        if (this.UserEmail.equals("test1@gmail.com")){
            my_image_id = R.drawable.moshe;
        }
        if (this.UserEmail.equals("test2@gmail.com")){
            my_image_id = R.drawable.noam;
        }
        if (this.UserEmail.equals("test3@gmail.com")){
            my_image_id = R.drawable.nadia;
        }
        if (this.UserEmail.equals("test4@gmail.com")){
            my_image_id = R.drawable.daniel;
        }
        img.setImageResource(my_image_id);

        Button exit_button = (Button) dialog.findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button send_message = (Button) dialog.findViewById(R.id.send_message);
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showChatPage();
                //build_and_run_alert();
            }
        });

        Button show_details = (Button) dialog.findViewById(R.id.show_details);
        show_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDetailsPopup();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * The detail for the responding user (from marker click)
     */
    public void showUserDetailsPopup(){

        // create a Dialog component
        final Dialog dialog = new Dialog(this.connectedContext);

        //tell the Dialog to use the userview.xml as it's layout description
        dialog.setContentView(R.layout.userdetails);
        //dialog.setTitle("Android Custom Dialog Box");

        Button exit_button = (Button) dialog.findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Show corresponding chat activity according to the marker that was clicked
     */
    public void showChatPage(){
        Intent intent = new Intent();
        intent.putExtra("myUsername", "aa");
        intent.putExtra("remoteUsername", "bb");
        intent.setClass(this.connectedContext, ChatActivity.class);
        this.connectedContext.startActivity(intent);
    }
}
