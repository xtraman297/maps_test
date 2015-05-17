package noam.socialbridge_alfa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

/**
 * Created by MrJellyB on 29/04/2015.
 */
public abstract class AlertPub extends Callback implements DialogInterface.OnClickListener {
    // Alert protected data members
    protected Context connectedContext;
    protected Pubnub pubnub;
    protected String strChannel;
    protected AlertDialog.Builder alert;

    /**
     * The base constructor for classes: {@link noam.socialbridge_alfa.AlertPubGet}
     * {@link noam.socialbridge_alfa.AlertPubSend}, used to build alert objects for listening and
     * publishing messages between users
     * @param connectedContext  - {@link android.content.Context} For getting activity resources
     * @param strChannel        - The pubnub channel to publish from
     * @param publisher         - {@link com.pubnub.api.Pubnub} the pubnub object to publish or subscribe
     */
    public AlertPub(Context connectedContext, String strChannel, Pubnub publisher) {
        this(connectedContext, strChannel);
        this.pubnub = publisher;
    }

    /**
     * The base constructor for classes: {@link noam.socialbridge_alfa.AlertPubGet}
     * {@link noam.socialbridge_alfa.AlertPubSend}, used to build alert objects for listening and
     * publishing messages between users
     * @param connectedContext  - {@link android.content.Context} For getting activity resources
     * @param strChannel        - The pubnub channel to publish from
     */
    public AlertPub(Context connectedContext, String strChannel) {
        this.connectedContext = connectedContext;
        this.strChannel = strChannel;

        try {
            ApplicationInfo aiMetaData = this.connectedContext.getPackageManager()
                    .getApplicationInfo(this.connectedContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            this.pubnub =
                    new Pubnub(aiMetaData.metaData.get("pubnubAPI_publish").toString(),
                            aiMetaData.metaData.get("pubnubAPI_subscribe").toString());
        }
        catch (PackageManager.NameNotFoundException exception) {
            System.out.println("MapObject.MapObject " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    public abstract void onClick(DialogInterface dialog, int which);
}
