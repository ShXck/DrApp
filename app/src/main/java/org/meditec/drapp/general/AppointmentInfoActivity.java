package org.meditec.drapp.general;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

public class AppointmentInfoActivity extends AppCompatActivity {

    private CheckBox check_box;
    private ListView management_list;
    private TextView patient_text;
    private TextView date_text;
    private ArrayAdapter adapter;
    private Button update_button;

    private String symptoms;
    private String clinic_case;
    private String tests;
    private String medication;
    private String patient_name;

    private ArrayAdapter<String> cases_adapter;
    private ArrayAdapter<String> tests_adapter;
    private ArrayAdapter<String> medication_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info);

        String[] options = {"Ver síntomas", "Editar medicación", "Editar exámenes", "Seleccionar caso clínico"};
        check_box = (CheckBox) findViewById(R.id.done_button);
        management_list = (ListView)findViewById(R.id.managament_list);
        patient_text = (TextView)findViewById(R.id.patient_label);
        date_text = (TextView)findViewById(R.id.date_label);
        update_button = (Button)findViewById(R.id.update_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        management_list.setAdapter(adapter);
        cases_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        medication_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        tests_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        medication = "";
        tests = "";
        set_ui();
        get_info();
        get_option_clicked();
    }

    /**
     * prepara la interfaz con la información de la cita.
     */
    private void set_ui() {
        RequestManager.wait_for_response(1000);
        JSONObject info = JSONHandler.parse(RequestManager.GET_REQUEST_DATA());
        try {
            patient_text.setText("Paciente: " + info.getString("patient"));
            patient_name = info.getString("patient");
            date_text.setText("Fecha: " + info.getString("day") + "/" + info.getString("month") + "/" + info.getString("year"));
            symptoms = info.getString("symptoms");
            clinic_case = info.getString("cases");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * listeners de la lista, botones y checkbox.
     */
    private void get_option_clicked() {
        management_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    show_dialog(position);
                }else {
                    show_symptoms_dialog();
                }
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clinic_case == null || clinic_case.equals("")){
                    show_dialog();
                }else {
                    send_updated_info();
                    Toast.makeText(getApplicationContext(), "La información ha sido actualizada", Toast.LENGTH_SHORT).show();
                    Intent menu = new Intent(AppointmentInfoActivity.this, MainMenuActivity.class);
                    menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(menu);
                }
            }
        });

        check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
                    Toast.makeText(getApplicationContext(), "Has terminado la cita", Toast.LENGTH_SHORT).show();
                    RequestManager.DELETE(HomePageActivity.identifier + "/appointments/" + patient_name, "");
                    Intent menu = new Intent(AppointmentInfoActivity.this, MainMenuActivity.class);
                    menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(menu);
                }
            }
        });
    }

    /**
     * Muetsra un mensaje para editar detalles de la cita.
     * @param position la posición del item que se escogió.
     */
    private void show_dialog(final int position){

        switch (position){
            case 1:
                show_medication_list();
                break;
            case 2:
                show_tests_list();
                break;
            case 3:
                show_cases_list_dialog();
                break;
        }
    }

    /**
     * Muestra un mensaje cuando se quiere realizar una acción inválida.
     */
    private void show_dialog(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Acción inválida");
        dialog.setMessage("Tienes que seleccionar un caso clínico primero.");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     *  Muestra los síntomas registrados por el usuario.
     */
    private void show_symptoms_dialog(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Síntomas registrados");
        dialog.setMessage(symptoms);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Muestra una lista de casos clínicos.
     */
    private void show_cases_list_dialog(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Caso Clínico");
        dialog.setMessage("Elige un caso clínico");

        final ListView cases = new ListView(this);
        cases.setAdapter(cases_adapter);
        dialog.setView(cases);

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        cases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String)parent.getItemAtPosition(position);
                if (clinic_case == null){
                    clinic_case = selected;
                }else {
                    clinic_case += selected + ",";
                }
            }
        });
        dialog.show();
    }

    /**
     * Muestra una lista de exámenes médicos.
     */
    private void show_tests_list(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Exámenes Médicos");
        dialog.setMessage("Elige los exámenes");

        final ListView tests_list = new ListView(this);
        tests_list.setAdapter(tests_adapter);
        dialog.setView(tests_list);

        dialog.setNegativeButton("Listo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        tests_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String)parent.getItemAtPosition(position);
                /*if (tests == null){
                    tests = selected + ",";
                }else {
                    tests += selected + ",";
                }*/
                tests += selected + ",";
            }
        });
        dialog.show();
    }

    /**
     * Muestra una lista de medicamentos.
     */
    private void show_medication_list(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Medicamentos disponibles");
        dialog.setMessage("Elige los medicamentos");

        final ListView medication_list = new ListView(this);
        medication_list.setAdapter(medication_adapter);
        dialog.setView(medication_list);

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        medication_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String)parent.getItemAtPosition(position);
                /*if (medication == null){
                    medication = selected + ",";
                }else {
                    medication += selected + ",";
                }*/
                medication += selected + ",";
            }
        });
        dialog.show();
    }


    /**
     * procesa la lista de casos que viene desde el servidor.
     * @param list la lista en json.
     */
    private void populate_cases_adapter(String list) {
        try {
            JSONObject cases_list = new JSONObject(list);
            JSONArray array = cases_list.getJSONArray("cases");

            for (int i = 0; i < array.length(); i++){
                cases_adapter.add((String) array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * procesa la lista de examenes que viene desde el servidor.
     * @param list la lista en json.
     */
    private void populate_tests_adapter(String list) {
        try {
            JSONObject cases_list = new JSONObject(list);
            JSONArray array = cases_list.getJSONArray("tests");

            for (int i = 0; i < array.length(); i++){
                tests_adapter.add((String) array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * procesa la lista de medicamentos que viene desde el servidor.
     * @param list la lista en json.
     */
    private void populate_medication_adapter(String list) {
        try {
            JSONObject cases_list = new JSONObject(list);
            JSONArray array = cases_list.getJSONArray("medication");

            for (int i = 0; i < array.length(); i++){
                medication_adapter.add((String) array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Petición para obtener las lista de casos, examenes y medicamentos.
     */
    private void get_info(){
        RequestManager.GET("cases");
        RequestManager.wait_for_response(1000);
        populate_cases_adapter(RequestManager.GET_REQUEST_DATA());

        RequestManager.GET("tests");
        RequestManager.wait_for_response(1000);
        populate_tests_adapter(RequestManager.GET_REQUEST_DATA());

        RequestManager.GET("medication");
        RequestManager.wait_for_response(1000);
        populate_medication_adapter(RequestManager.GET_REQUEST_DATA());


    }

    /**
     * Petición para actualizar la información en el servidor.
     */
    private void send_updated_info(){
        Log.i("json",JSONHandler.build_appointment_info(medication, tests, clinic_case));
        RequestManager.PUT(String.valueOf(HomePageActivity.identifier) + "/appointments/" + patient_name, JSONHandler.build_appointment_info(medication, tests, clinic_case));
    }
}
