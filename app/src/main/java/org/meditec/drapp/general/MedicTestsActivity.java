package org.meditec.drapp.general;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.RequestManager;

import java.util.ArrayList;

public class MedicTestsActivity extends AppCompatActivity {

    private ListAdapter adapter;
    private ArrayList<String> tests_list = new ArrayList<>();

    private ListView list_view_tests;
    private EditText name_field;
    private EditText price_field;
    private Button create_button;

    private String name_detail;
    private String cost_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_tests);

        get_tests();

        list_view_tests = (ListView)findViewById(R.id.tests_list);
        name_field = (EditText)findViewById(R.id.name_field);
        price_field = (EditText)findViewById(R.id.price_field);
        create_button = (Button)findViewById(R.id.create_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tests_list);
        list_view_tests.setAdapter(adapter);

        get_click();
    }

    private void get_click() {
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void get_tests() {
        RequestManager.GET("tests");
        RequestManager.wait_for_response(500);
        process_list(RequestManager.GET_REQUEST_DATA());
    }

    private void process_list(String json_list){
        try {
            JSONObject list = new JSONObject(json_list);

            for (int i = 0; i < list.getInt("count"); i++){
                tests_list.add(list.getString(String.valueOf(i + 1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
