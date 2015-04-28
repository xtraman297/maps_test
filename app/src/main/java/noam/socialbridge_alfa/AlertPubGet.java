package noam.socialbridge_alfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pubnub.api.Callback;
import com.pubnub.api.PubnubException;

/**
 * Created by MrJellyB on 29/04/2015.
 */
public class AlertPubGet extends AlertPub implements Runnable {
    public AlertPubGet(Context connectedContext, String strChannel) {
        super(connectedContext, strChannel);

        try {
            this.pubnub.subscribe(strChannel, this);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

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
