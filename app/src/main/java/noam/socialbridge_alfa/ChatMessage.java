package noam.socialbridge_alfa;

import android.app.Activity;
import android.content.Context;

import com.pubnub.api.Callback;

import java.text.DateFormat;

/**
 * Created by Technovibe on 17-04-2015.
 */
public class ChatMessage extends Callback implements Runnable {
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private Context connectedContext;

    public ChatMessage(Context connectedContext) {
        this.connectedContext = connectedContext;
    }

    public ChatMessage(Context connectedContext,
                       String message,
                       Long userId,
                       String dateTime,
                       boolean isMe) {
        this(connectedContext);
        this.message = message;
        this.userId = userId;
        this.dateTime = dateTime;
        this.isMe = isMe;
        this.id = 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsme() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public void successCallback(String channel, Object message) {
        super.successCallback(channel, message);
        this.message = message.toString();
        ((Activity)this.connectedContext).runOnUiThread(this);
    }

    @Override
    public void run() {
        ((ChatActivity)this.connectedContext).displayMessage(this);
    }
}
