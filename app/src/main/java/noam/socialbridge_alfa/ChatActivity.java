package noam.socialbridge_alfa;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by USER on 13/05/2015.
 */
public class ChatActivity extends Activity {

    private Handler handler = new Handler();
    public ListView msgView;
    public ArrayAdapter<String> msgList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);
        String[] conversationTemp = { "Hi,", "Hi to you too, how are you?", "I'm fine, how are you", "I'm ok :)" };
        displayAllMessages(conversationTemp);
        msgView = (ListView) findViewById(R.id.listView);

        msgList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);

//		msgView.smoothScrollToPosition(msgList.getCount() - 1);

        Button btnSend = (Button) findViewById(R.id.btn_Send);

        //receiveMsg("");
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                final EditText txtEdit = (EditText) findViewById(R.id.txt_inputText);
                msgList.add(txtEdit.getText().toString());
                sendMessageToServer(txtEdit.getText().toString(), "myemail", "hisemail");
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
                txtEdit.setText("");

            }
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


    public void receiveMsg(String msg) {
        // This function should be called when receiving the message from PubNub,?
        displayMsg(msg);

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
}

