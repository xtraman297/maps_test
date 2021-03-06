package noam.socialbridge_alfa;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;

/**
 * Created by MrJellyB on 13/03/2015.
 * a Static class that gives services for contacting SocialBridge API
 */
public final class SocialBridgeActionsAPI extends FragmentActivity {

    /**
     * This generic method is responsible of handling all GET requests
     * to the SB server (such as 'users', 'messages' and so)
     * @param strAction - the action to get info from the server ('users', specific user)
     * @param resources - necessary resources context to get the server and version values
     *                    THIS IS REQUIRED
     * @return - {@link JSONArray} array that contains all the returned info.
     */
    public static JSONArray GetRequest(String strAction, Context resources) {
        // Get necessary vars from the resources
        String strServerIP = resources.getResources().getString(R.string.ServerIP);
        String strServerVer = resources.getResources().getString(R.string.ServerVer);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(strServerIP + strAction);
        request.addHeader("Accept",
                          "application/vnd.SB-API." + strServerVer + "+json");
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

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR:", e.toString());
            return null;
        }
        return json;
    }

    /**
     * The main method for making API requests to the server.
     * @param strAction The name of the action according to the server's available actions
     *                  ('user', 'user/{id}').
     *                  @see <a href="http://google.com">http://docs.socialbridge.apiary.io/</a>
     * @param params    The additional parameters to use with the request
     * @param resources Activity or other kind of context is needed for accessing resources
     *                  within the application ({@link R}
     */
    public static void SendPutUpdate(String strAction, StringEntity params, Context resources){
        // Get necessary vars from the resources
        String strServerIP = resources.getResources().getString(R.string.ServerIP);
        String strServerVer = resources.getResources().getString(R.string.ServerVer);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPut request = new HttpPut(strServerIP + strAction);
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "application/vnd.SB-API." + strServerVer + "+json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseText = null;
            responseText = EntityUtils.toString(response.getEntity());
            System.out.println(strServerIP + strAction);
            System.out.println(responseText);
            System.out.println(request.getEntity());
            // handle response here...
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR:", e.toString());
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
    }


    public static HttpResponse SendPostMessage(String strAction, StringEntity params, Context resources){
        // Get necessary vars from the resources
        String strServerIP = resources.getResources().getString(R.string.ServerIP);
        String strServerVer = resources.getResources().getString(R.string.ServerVer);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpClient = new DefaultHttpClient();


        try {
            HttpPost request = new HttpPost(strServerIP + strAction);

            //StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "application/vnd.SB-API." + strServerVer + "+json");
            request.setEntity(params);
            return(httpClient.execute(request));
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR:", e.toString());
            return null;
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /***
     * Method to update user's position on the server
     * @param strUserName - the name of the user (nickname)
     * @param ltnLocation - {@link com.google.android.gms.maps.model.LatLng} the new location
     * @param connectedContext - necessary resources context to get the server and version values
     *                    THIS IS REQUIRED
     */
    public static void updateUserLocation(String strUserName,
                                          LatLng ltnLocation,
                                          Context connectedContext) {
        strUserName = Uri.encode(strUserName.substring(0, strUserName.lastIndexOf('.'))) + "/" +
                strUserName.substring(strUserName.lastIndexOf('.') + 1);

        String strEntityFormat = String.format(
                "{\"user\"" +
                        ":{\"user_name\":\"%s\"," +
                        "\"location_attributes\":" +
                        "{\"latitude\":%f," +
                        "\"longitude\":%f}}}",
                strUserName,
                ltnLocation.latitude,
                ltnLocation.longitude);

        StringEntity seToSend;

        try {
            seToSend = new StringEntity(strEntityFormat);

            // Send the new position to the server
            SocialBridgeActionsAPI.SendPutUpdate("user/update/" + strUserName,
                    seToSend,
                    connectedContext);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
