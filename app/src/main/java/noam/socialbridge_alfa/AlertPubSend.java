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
 */
public class AlertPubSend extends AlertPub
        implements GoogleMap.OnMarkerClickListener{

    public EditText input;
    private Marker myMarker;
    public String UserEmail;

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
        this.UserEmail = marker.getTitle();
        //this.build_and_run_alert();
        this.showUserPopup();
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
    public void showChatPage(){
        Intent intent = new Intent();
        intent.setClass(this.connectedContext, ChatActivity.class);
        this.connectedContext.startActivity(intent);
    }
}
