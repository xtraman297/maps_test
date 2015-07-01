package noam.socialbridge_alfa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MrJellyB on 16/06/2015.
 */
public class Globals {
    public static String UserName;
    public static String UserEmail;
    
    // Helper Methods

    /**
     * Concatinate between several JSONArrays
     * @param arr   - array of JSONArrays
     * @return      - the concatinated result
     */
    public static JSONArray joinArrays(JSONArray... arr) {
        JSONArray jaResult = new JSONArray();

        try {
            for (JSONArray arrArray : arr) {
                for (int nObject = 0; nObject < arrArray.length(); nObject++) {

                        jaResult.put(arrArray.get(nObject));

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jaResult;
    }
}
