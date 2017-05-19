package org.meditec.drapp.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.RequestManager;

import java.util.ArrayList;

public class AppointmentsActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private ListView appointment_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        appointment_list = (ListView)findViewById(R.id.appointment_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        appointment_list.setAdapter(adapter);

        get_agenda();
        get_selected_appointment();
    }

    /**
     * listener del butón.
     */
    private void get_selected_appointment() {
        appointment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String appointment_selected = (String) parent.getItemAtPosition(position);
                get_appointment(appointment_selected);
            }
        });
    }

    /**
     * Petición para obtener la agenda.
     */
    private void get_agenda() {
        RequestManager.GET(String.valueOf(HomePageActivity.identifier) + "/appointments");
        RequestManager.wait_for_response(1000);
        process_list(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Petición para obtener una cita especifica.
     * @param appointment la cita a buscar.
     */
    private void get_appointment(String appointment){
        RequestManager.GET(String.valueOf(HomePageActivity.identifier) + "/appointments/" + appointment);
        Intent appointment_info = new Intent(this, AppointmentInfoActivity.class);
        appointment_info.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(appointment_info);
    }

    /**
     * Procesa la lista de citas.
     * @param list la lista a procesar.
     */
    private void process_list(String list) {
        try{
            JSONObject json = new JSONObject(list);
            JSONArray array = json.getJSONArray("appointments");

            for (int i = 0; i < array.length(); i++){
                adapter.add(array.get(i));
            }
        }catch (JSONException j){
            j.printStackTrace();
        }
    }
}
