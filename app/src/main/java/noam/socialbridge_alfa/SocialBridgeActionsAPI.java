package noam.socialbridge_alfa;

import android.content.Context;
import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by MrJellyB on 13/03/2015.
 * a Static class that gives services for contacting SocialBridge API
 */
public final class SocialBridgeActionsAPI {

    public static JSONArray GetRequest(String strAction, Context resources) {
        String serverIP = resources.getResources().getText(R.string.ServerIP).toString();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(serverIP + strAction);
        HttpResponse response;
        JSONArray json;
        json = null;
        try {
            response = client.execute(request);
            System.out.println(response.getStatusLine());
            String responseText = null;
            responseText = EntityUtils.toString(response.getEntity());
            System.out.println(responseText);
            json = null;
            try {
                json = new JSONArray(responseText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    public static void SendPutUpdate(String strAction, StringEntity params, Context resources){
        String serverIP = resources.getResources().getText(R.string.ServerIP).toString();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPut request = new HttpPut(serverIP + strAction);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseText = null;
            responseText = EntityUtils.toString(response.getEntity());
            System.out.println(serverIP + strAction);
            System.out.println(responseText);
            System.out.println(request.getEntity());
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

}
