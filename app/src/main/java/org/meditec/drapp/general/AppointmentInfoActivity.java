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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ListAdapter adapter;
    private Button update_button;

    private String symptoms;
    private String clinic_case;
    private String tests;
    private String medication;
    private String patient_name;

    private ArrayAdapter<String> dialog_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info);

        String[] options = {"Editar síntomas", "Editar medicación", "Editar exámenes", "Seleccionar casos existentes"};
        check_box = (CheckBox) findViewById(R.id.done_button);
        management_list = (ListView)findViewById(R.id.managament_list);
        patient_text = (TextView)findViewById(R.id.patient_label);
        date_text = (TextView)findViewById(R.id.date_label);
        update_button = (Button)findViewById(R.id.update_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        management_list.setAdapter(adapter);
        dialog_list = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        clinic_case = "";
        set_ui();
        get_cases_list();
        get_option_clicked();
    }

    private void set_ui() {
        RequestManager.wait_for_response(500);
        JSONObject info = JSONHandler.parse(RequestManager.GET_REQUEST_DATA());
        try {
            patient_text.setText("Paciente: " + info.getString("patient"));
            patient_name = info.getString("patient");
            date_text.setText("Fecha: " + info.getString("day") + "/" + info.getString("month") + "/" + info.getString("year"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get_option_clicked() {
        management_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <= 2) {
                    show_dialog(position, (String)parent.getItemAtPosition(position));
                }else {
                    show_cases_list_dialog();
                }
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_updated_info();
                Toast.makeText(getApplicationContext(), "La información ha sido actualizada", Toast.LENGTH_SHORT);

                Intent menu = new Intent(AppointmentInfoActivity.this, MainMenuActivity.class);
                startActivity(menu);
            }
        });

        check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
                    Toast.makeText(getApplicationContext(), "Has terminado la cita", Toast.LENGTH_SHORT);
                    RequestManager.DELETE(HomePageActivity.identifier + "/appointments/" + patient_name, "");
                    Intent menu = new Intent(AppointmentInfoActivity.this, MainMenuActivity.class);
                    startActivity(menu);
                }
            }
        });
    }

    private void show_dialog(final int position, String option){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(option);
        dialog.setMessage("Introduce el contenido, si son varias especificaciones sepárelos por coma.");

        final EditText edit_text = new EditText(this);
        dialog.setView(edit_text);
        dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save_info(position,edit_text);
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void save_info(int position, EditText text_field){
        switch (position){
            case 0:
                symptoms = text_field.getText().toString();
                break;
            case 1:
                medication = text_field.getText().toString();
                break;
            case 2:
                tests = text_field.getText().toString();
                break;
        }
    }

    private void show_cases_list_dialog(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Caso Clínico");
        dialog.setMessage("Elige un caso clínico");

        final ListView cases = new ListView(this);
        cases.setAdapter(dialog_list);
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
                if (clinic_case.equals("")){
                    clinic_case += selected;
                }else {
                    clinic_case += "," + selected;
                }
            }
        });
        dialog.show();
    }

    private void populate_adapter(String list) {
        try {
            JSONObject cases_list = new JSONObject(list);
            for (int i = 0; i < cases_list.getInt("count"); i++){
                dialog_list.add(cases_list.getString(String.valueOf(i + 1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get_cases_list(){
        RequestManager.GET("cases");
        RequestManager.wait_for_response(500);
        populate_adapter(RequestManager.GET_REQUEST_DATA());
    }

    private void send_updated_info(){
        Log.d("Path", String.valueOf(HomePageActivity.identifier) + "/appointments/" + patient_name);
        Log.i("Info", JSONHandler.build_appointment_info(symptoms, medication, tests, clinic_case));
        RequestManager.PUT(String.valueOf(HomePageActivity.identifier) + "/appointments/" + patient_name, JSONHandler.build_appointment_info(symptoms, medication, tests, clinic_case));
    }
}
