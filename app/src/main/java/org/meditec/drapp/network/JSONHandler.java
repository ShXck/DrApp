package org.meditec.drapp.network;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHandler {

    public static String get_json_med_info(String name, String email){


        JSONObject med_info = new JSONObject();

        try {
            med_info.put("name", name);
            med_info.put("email", email);
        }catch (JSONException j){
            j.printStackTrace();
        }
        return med_info.toString();
    }
}
