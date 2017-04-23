package org.meditec.drapp.general;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.meditec.drapp.R;

public class MainMenuActivity extends AppCompatActivity {

    private ListView menu;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        menu = (ListView)findViewById(R.id.main_menu);

        String[] menu_options = {"Clinic Cases", "Tests Management", "Medication Management", "Chat", "Search Clinic Cases", "Agenda", "Appointments", "Client's Feedback"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu_options);

        menu.setAdapter(adapter);

        get_button_clickced();
    }

    private void get_button_clickced() {

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    //open clinic cases
                }
            }
        });
    }
}
