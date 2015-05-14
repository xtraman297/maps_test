package noam.socialbridge_alfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by USER on 14/05/2015.
 */
public class ChooseUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDialog();
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] users = {
                "Moshe",
                "Noam",
                "Nadia",
                "Daniel"
        };
        final String[] mymails = {
                "test1@gmail.com",
                "test2@gmail.com",
                "test3@gmail.com",
                "test4@gmail.com"
        };
        builder.setTitle("Pick a user")
                .setItems(users, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        //MyEmail = mymails[which];
                        startMapsActivity(mymails[which]);
                        finish();

                    }
                });
        builder.show();
        return builder.create();

    }
    public void startMapsActivity(String MyEmail){
        Intent intent = new Intent();

        intent.setClass(this, MapsActivity.class);
        intent.putExtra("email", MyEmail);
        this.startActivity(intent);
    }
}
