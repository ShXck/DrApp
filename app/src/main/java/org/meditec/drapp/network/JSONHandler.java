package org.meditec.drapp.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

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

    public static String get_appointment_info(String symptoms, String medication, String tests, String clinic_cases, String case_name){

        JSONObject info = new JSONObject();
        try {
            info.put("symptoms", symptoms);
            info.put("medication", medication);
            info.put("tests", tests);
            info.put("name", case_name);

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

    public static String build_json_case(String name, String medication, String tests){

        JSONObject json_case = new JSONObject();
        try {
            json_case.put("name", name);
            json_case.put("medication", medication);
            json_case.put("tests", tests);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json_case.toString();
    }

    public static JSONObject parse_clinic_case_details(String json_case){

        JSONObject details = null;
        try {
            details = new JSONObject(json_case);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static String build_new_test(String name, String cost){
        JSONObject test = new JSONObject();
        try {
            test.put("name", name);
            test.put("cost",cost);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return test.toString();
    }
}
