package noam.socialbridge_alfa;

import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);

        msgView = (ListView) findViewById(R.id.listView);


        //loadMyHistory();
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        msgView.setAdapter(adapter);
        addMessagesHistory();

        //String[] conversationTemp = { "Hi,", "Hi to you too, how are you?", "I'm fine, how are you", "I'm ok :)" };
        //displayAllMessages(conversationTemp);


        //msgList = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1);
        //msgView.setAdapter(msgList);

//		msgView.smoothScrollToPosition(msgList.getCount() - 1);

        Button btnSend = (Button) findViewById(R.id.btn_Send);

        //receiveMsg("");
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                    final EditText txtEdit = (EditText) findViewById(R.id.txt_inputText);
                    String messageText = txtEdit.getText().toString();
                    if (TextUtils.isEmpty(messageText)) {
                        return;
                    }

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(122);//dummy
                    chatMessage.setMessage(messageText);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);

                    txtEdit.setText("");
                    updateMessage(messageText);
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

    public void displayAllMessages(String myConversation[]){

        for( int i = 0; i < myConversation.length - 1; i++)
        {
            displayMsg(myConversation[i]);
        }
    }

    public void sendMessageToServer(String str, String local_user, String remote_user) {
        //Here we should send the message to the server
        //We should use both local and remote user ids to identify the conversation
        System.out.println(str);

    }
    public void updateMessage(String msg){
        SocialBridgeActionsAPI.SendPostMessage("messages", msg, this);
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
        JSONArray jsonMyConversation = SocialBridgeActionsAPI.GetRequest("messages", this);
        for (int place = 0; place < jsonMyConversation.length(); place++) {
            try {
                JSONObject joCurr = ((JSONObject)jsonMyConversation.get(place));
                ChatMessage chatMessage = new ChatMessage();
                int msgId = Integer.parseInt(joCurr.get("id").toString());
                System.out.println(msgId);
                chatMessage.setId(msgId);//dummy
                chatMessage.setMessage(joCurr.get("body").toString());
                System.out.println(joCurr.get("body").toString());
                chatMessage.setDate(joCurr.get("created_at").toString());
                if (joCurr.get("to_user_id").toString().equals("2")) {
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

    private void loadMyHistory(){

        chatHistory = new ArrayList<ChatMessage>();
        ChatMessage msg = new ChatMessage();

        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        ChatMessage msg1 = new ChatMessage();
        msg1.setId(2);
        msg1.setMe(false);
        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        msgView.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }

}

