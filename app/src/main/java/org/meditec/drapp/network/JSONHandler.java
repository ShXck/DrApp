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

    public static String deserialize_identifier(String json_identifier){
        try {
            JSONObject identifier = new JSONObject(json_identifier);
            return identifier.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get_appointment_info(String symptoms, String medication, String tests, String clinic_cases){

        JSONObject info = new JSONObject();
        try {
            info.put("symptoms", symptoms);
            info.put("medication", medication);
            info.put("tests", tests);

            if (clinic_cases != null) info.put("clinic_cases", clinic_cases);
            else info.put("clinic_cases", "none");
        }catch (JSONException j){
            j.printStackTrace();
        }
        return info.toString();
    }

    public static JSONObject parse(String info){
        try {
            return new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
