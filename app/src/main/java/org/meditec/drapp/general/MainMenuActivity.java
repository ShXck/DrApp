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

import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

public class MainMenuActivity extends AppCompatActivity {

    private ListView menu;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        set_user_information();

        menu = (ListView)findViewById(R.id.main_menu);

        String[] menu_options = {"Casos clínicos", "Manejo de Exámenes Médicos", "Manejo de Medicamentos", "Chat", "Agenda", "Feedback"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu_options);

        menu.setAdapter(adapter);

        get_button_clickced();
    }

    private void set_user_information() {
        Log.d("ID", RequestManager.GET_REQUEST_DATA());
        if (HomePageActivity.identifier == null) HomePageActivity.identifier = JSONHandler.deserialize_identifier(RequestManager.GET_REQUEST_DATA());
    }

    private void get_button_clickced() {

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4){
                    Intent appointments = new Intent(MainMenuActivity.this , AppointmentsActivity.class);
                    startActivity(appointments);
                }else if (position == 0){
                    Intent clinic_cases_management = new Intent(MainMenuActivity.this, CasesManagementActivity.class);
                    startActivity(clinic_cases_management);
                }else if (position == 1){
                    Intent tests = new Intent(MainMenuActivity.this, MedicTestsActivity.class);
                    startActivity(tests);
                }
            }
        });
    }
}
