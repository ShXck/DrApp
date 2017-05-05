package org.meditec.drapp.general;

import android.content.DialogInterface;
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
    private String clinic_cases;
    private String tests;
    private String medication;
    private String patient_name;
    private String case_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info);

        String[] options = {"Editar síntomas", "Editar medicación", "Editar exámenes", "Seleccionar casos existentes", "Nombrar caso"};
        check_box = (CheckBox) findViewById(R.id.done_button);
        management_list = (ListView)findViewById(R.id.managament_list);
        patient_text = (TextView)findViewById(R.id.patient_label);
        date_text = (TextView)findViewById(R.id.date_label);
        update_button = (Button)findViewById(R.id.update_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        management_list.setAdapter(adapter);

        set_ui();

        get_option_clicked();

    }

    private void set_ui() {
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
                show_dialog(position);
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_updated_info();
                Toast.makeText(getApplicationContext(), "La información ha sido actualizada", Toast.LENGTH_SHORT);
            }
        });
    }

    private void show_dialog(final int position){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Gestión de cita");
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
            case 3:
                clinic_cases = "";
                break;
            case 4:
                case_name = text_field.getText().toString();
        }
    }

    private void send_updated_info(){
        Log.d("Path", String.valueOf(HomePageActivity.identifier) + "/appointments/" + patient_name);
        Log.i("Info", JSONHandler.get_appointment_info(symptoms, medication, tests, clinic_cases, case_name));
        RequestManager.PUT(String.valueOf(HomePageActivity.identifier) + "/appointments/" + patient_name, JSONHandler.get_appointment_info(symptoms, medication, tests, clinic_cases, case_name));
    }
}
