package org.meditec.drapp.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class JSONHandler {

    /**
     * Construye la informacion del médico en json.
     * @param name el nombre del médico.
     * @param email el email de médico.
     * @return un objeto json con la información del médico.
     */
    public static String build_json_med_info(String name, String email){

        JSONObject med_info = new JSONObject();

        try {
            med_info.put("name", name);
            med_info.put("email", email);
        }catch (JSONException j){
            j.printStackTrace();
        }
        return med_info.toString();
    }

    /**
     * @param json_identifier un json con un id.
     * @return el id en string.
     */
    public static String deserialize_identifier(String json_identifier){
        try {
            JSONObject identifier = new JSONObject(json_identifier);
            return identifier.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Construye un json con información de cita.
     * @param medication el tratamiento.
     * @param tests los exámenes.
     * @param clinic_cases los casos clinicos.
     * @return el json con la información de la cita.
     */
    public static String build_appointment_info(String medication, String tests, String clinic_cases){

        JSONObject info = new JSONObject();
        String message = "none";
        try {
            if (medication != "") info.put("medication", medication);
            else info.put("medication",message);

            if (tests != "") info.put("tests",tests);
            else info.put("tests", message);

            if (clinic_cases != null) info.put("cases", clinic_cases);
            else info.put("cases", "none");
        }catch (JSONException j){
            j.printStackTrace();
        }
        return info.toString();
    }

    /**
     * Parsea un objeto json en string cualquiera.
     * @param info el json en string.
     * @return un objeto json.
     */
    public static JSONObject parse(String info){
        try {
            return new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Construye un json con información de un caso clinico.
     * @param name el nombre del caso.
     * @param medication los medicamentos.
     * @param tests los examenes.
     * @return el objeto json con la información.
     */
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

    /**
     * Transforma un  json string a json
     * @param json_case
     * @return
     */
    public static JSONObject parse_clinic_case_details(String json_case){

        JSONObject details = null;
        try {
            details = new JSONObject(json_case);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

    /**
     * Construye un json con la información de un exámen.
     * @param name el nombre del examen.
     * @param cost el precio del examen.
     * @return el json con la información del examen.
     */
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

    /**
     * Construye un mensaje en json.
     * @param msg el mensaje.
     * @param code el codigo de médico.
     * @return un mensaje en json.
     */
    public static String build_msg(String msg, String code){
        JSONObject json_msg = new JSONObject();

        try {
            json_msg.put("sender", code);
            json_msg.put("msg", msg);
        }catch(JSONException j){
            j.printStackTrace();
        }
        return json_msg.toString();
    }
}
