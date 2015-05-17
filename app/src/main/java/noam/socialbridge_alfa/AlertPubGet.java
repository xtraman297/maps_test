package noam.socialbridge_alfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pubnub.api.Callback;
import com.pubnub.api.PubnubException;

/**
 * Created by MrJellyB on 29/04/2015.
 * This class is responsible for showing received messages to the current user.
 * TODO: also need to be singleton class as {@link noam.socialbridge_alfa.UserMapObject}
 */
public class AlertPubGet extends AlertPub implements Runnable {

    /**
     * This constructor builds alert listener object(this)
     * @param connectedContext - {@link android.content.Context} Context for accessing resources
     * @param strChannel       - The pubnub channel to subscribe to
     */
    public AlertPubGet(Context connectedContext, String strChannel) {
        super(connectedContext, strChannel);

        try {
            this.pubnub.subscribe(strChannel, this);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will be invoked when a button in the dialog is clicked.
     * @param dialog - The dialog that received the click.
     * @param which - The button that was clicked (e.g. BUTTON1) or the position of the item clicked.
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    /**
     *
     * @param channel
     * @param message
     */
    @Override
    public void successCallback(String channel, Object message) {
        this.alert = new AlertDialog.Builder(this.connectedContext)
                            .setMessage((String) message)
                            .setPositiveButton("OK", this);

        ((Activity)this.connectedContext).runOnUiThread(this);
    }


    @Override
    public void run() {
        this.alert.show();
    }
}
