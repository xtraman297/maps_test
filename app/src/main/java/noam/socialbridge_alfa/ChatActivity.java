package noam.socialbridge_alfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by USER on 13/05/2015.
 */
public class ChatActivity extends ActionBarActivity {

    private Handler handler = new Handler();
    public ListView msgView;
    public ArrayAdapter<String> msgList;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private Pubnub pubTube;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);
        msgView = (ListView) findViewById(R.id.listView);
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        msgView.setAdapter(adapter);
        addMessagesHistory();
        Button btnSend = (Button) findViewById(R.id.btn_Send);
        ApplicationInfo aiMetaData;

        try {
            aiMetaData = getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            this.pubTube =
                    new Pubnub(aiMetaData.metaData.get("pubnubAPI_publish").toString(),
                               aiMetaData.metaData.get("pubnubAPI_subscribe").toString());

            // Also subscribe for messages and display them
            try {
                this.pubTube.subscribe(Globals.UserName + "-chat",
                                            new ChatMessage(this,
                                                            "",
                                                            (long)213,
                                                            DateFormat.getDateTimeInstance().format(new Date()),
                                                            false));
            } catch (PubnubException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //receiveMsg("");
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    final EditText txtEdit = (EditText) findViewById(R.id.txt_inputText);
                    String messageText = txtEdit.getText().toString();
                    if (TextUtils.isEmpty(messageText)) {
                        return;
                    }

                    ChatMessage chatMessage = new ChatMessage(getBaseContext());
                    chatMessage.setId(122);//dummy
                    chatMessage.setMessage(messageText);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);

                    txtEdit.setText("");

                    // Get remote and local users emails and send the message
                    try {
                        sendMessage(
                                messageText,
                                getIntent().getExtras().getString("myUsername"),
                                getIntent().getExtras().getString("remoteUsername"),
                                getIntent().getExtras().getString("pubnub_channel")
                                );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                displayMessage(chatMessage);
                }
                /*
                final EditText txtEdit = (EditText) findViewById(R.id.txt_inputText);
                msgList.add(txtEdit.getText().toString());
                sendMessageToServer(txtEdit.getText().toString(), "myemail", "hisemail");
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
                txtEdit.setText("");*/


        });

//		receiveMsg();
        //----------------------------
        //server msg receieve
        //-----------------------


        //End Receive msg from server//
    }

    /**
     *
     * @param strMessage    -
     * @param local_user    -
     * @param remote_user   -
     * @param strChannel    -
     * @throws UnsupportedEncodingException
     */
    public void sendMessage(String strMessage, String local_user, String remote_user, String strChannel)
            throws UnsupportedEncodingException {
        //Here we should send the message to the server
        //We should use both local and remote user ids to identify the conversation
        System.out.println(strMessage);
        StringEntity streMsg = new StringEntity(String.format(
                "{" +
                        "\"message\":" +
                        "{" +
                            "\"from_user_name\":\"%s\"," +
                            "\"to_user_name\":\"%s\"," +
                            "\"body\":\"%s\"" +
                        "}" +
                "}",
                local_user,
                remote_user,
                strMessage
        ));

        // Save the message on the server
        SocialBridgeActionsAPI.SendPostMessage("messages", streMsg, this);

        // Send to other user through pubnub
        this.pubTube.publish(
                strChannel,
                strMessage,
                new Callback() {
                    @Override
                    public void successCallback(String channel, Object message) {
                        super.successCallback(channel, message);

//                        new AlertDialog.Builder(this.connectedContext)
//                                .setMessage((String) message)
//                                .setPositiveButton("OK", this);
                    }
                }
        );
    }

    public void receiveMsg(String msg) {
        // This function should be called when receiving the message from PubNub,?
        displayMsg(msg);

    }
    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    private void scroll() {
        msgView.setSelection(msgView.getCount() - 1);
    }

    public void displayMsg(String msg) {
        final String mssg = msg;
        handler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                msgList.add(mssg);
                msgView.setAdapter(msgList);
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
                Log.d("", "hi");
            }
        });

    }

    private void addMessagesHistory(){
        // Set vars for request
        // TODO: Make server return id
        String strQuery = String.format("from_user_name=%s&to_user_name=%s",
                                        Globals.UserName,
                                        getIntent().getExtras().getString("remoteUsername"));
        JSONArray jsonMyConversation =
                SocialBridgeActionsAPI.GetRequest("messages?" + strQuery, this);

        // For every message received from server, add it to the conversation
        for (int place = 0; place < jsonMyConversation.length(); place++) {
            try {
                JSONObject joCurr = ((JSONObject)jsonMyConversation.get(place));
                ChatMessage chatMessage = new ChatMessage(this);
                chatMessage.setMessage(joCurr.get("body").toString());
                System.out.println(joCurr.get("body").toString());
                chatMessage.setDate(joCurr.get("created_at").toString());
                if (((JSONObject)joCurr.get("to_user")).get("user_name").toString().equals(Globals.UserName)) {
                    chatMessage.setMe(true);
                }
                else{
                    chatMessage.setMe(false);
                }
                displayMessage(chatMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

