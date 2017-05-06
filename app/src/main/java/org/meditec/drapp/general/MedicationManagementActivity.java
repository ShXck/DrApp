package org.meditec.drapp.general;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

import java.util.ArrayList;

public class MedicationManagementActivity extends AppCompatActivity {

    private Button create_button;
    private ListAdapter adapter;
    private ListView medication_list;
    private ArrayList<String> names = new ArrayList<>();
    private EditText name_field;
    private EditText price_field;

    private String name_detail;
    private String price_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_management);

        names.clear();
        get_medication_list();

        create_button = (Button) findViewById(R.id.button);
        medication_list = (ListView)findViewById(R.id.medication_list);
        name_field = (EditText)findViewById(R.id.name);
        price_field = (EditText)findViewById(R.id.cost);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, names);
        medication_list.setAdapter(adapter);

        get_click();
    }

    private void get_click() {

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestManager.POST("medication/new_medication", JSONHandler.build_new_test(name_field.getText().toString(), price_field.getText().toString()));
            }
        });

        medication_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String test_chosen = (String)parent.getItemAtPosition(position);
                PopupMenu popup_menu = new PopupMenu(MedicationManagementActivity.this, view);
                popup_menu.getMenuInflater().inflate(R.menu.popup_menu, popup_menu.getMenu());
                popup_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Eliminar")){
                            show_delete_dialog(test_chosen);
                        }else if (item.getTitle().equals("Editar")){
                            get_medication_details(test_chosen);
                            show_edit_dialog(test_chosen);
                        }else{
                            get_medication_details(test_chosen);
                            show_overview_dialog(test_chosen);
                        }
                        return true;
                    }
                });
                popup_menu.show();
            }
        });
    }

    private void show_delete_dialog(final String medication_name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Eliminar " + medication_name);
        dialog.setMessage("¿Está seguro de que quiere eliminar " + medication_name + " . La decisión es final.");

        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestManager.DELETE("medication/" + medication_name, "{}");
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void show_edit_dialog(final String medication_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Editar " + medication_name);
        dialog.setMessage("Edita el precio y el nombre en los espacios");

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name_field = new EditText(this);
        name_field.setText(name_detail);
        layout.addView(name_field);

        final EditText cost_field = new EditText(this);
        cost_field.setText(price_detail);
        layout.addView(cost_field);

        dialog.setView(layout);

        dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestManager.PUT("medication/" + medication_name, JSONHandler.build_new_test(name_field.getText().toString(), cost_field.getText().toString()));
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

    private void show_overview_dialog(final String medication_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(medication_name);
        dialog.setMessage("Name: " + name_detail + "\n" + "Costo total: " + price_detail);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void get_medication_details(String medication_name){
        RequestManager.GET("medication/" + medication_name);
        RequestManager.wait_for_response(500);
        JSONObject json_med = JSONHandler.parse(RequestManager.GET_REQUEST_DATA());
        try {
            name_detail = json_med.getString("name");
            price_detail = String.valueOf(json_med.getInt("cost"));
        }catch (JSONException j){
            j.printStackTrace();
        }
    }

    private void get_medication_list() {
        RequestManager.GET("medication");
        RequestManager.wait_for_response(500);
        process_list(RequestManager.GET_REQUEST_DATA());
    }

    private void process_list(String list) {
        try {
            JSONObject json_list = new JSONObject(list);

            for (int i = 0; i < json_list.getInt("count"); i++){
                names.add(json_list.getString(String.valueOf(i + 1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
