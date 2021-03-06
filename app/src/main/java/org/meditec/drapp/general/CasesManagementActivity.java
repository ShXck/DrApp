package org.meditec.drapp.general;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

import java.util.ArrayList;

public class CasesManagementActivity extends AppCompatActivity {

    private EditText name_field;
    private EditText medication_field;
    private EditText tests_field;
    private ArrayAdapter adapter;
    private ListView cases_list;
    private Button create_button;

    private String medication_detail;
    private String tests_detail;
    private String price_detail;
    private String height_detail;
    private String depth_detail;
    private String path_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases_management);

        name_field = (EditText) findViewById(R.id.name);
        medication_field = (EditText) findViewById(R.id.medication_field);
        tests_field = (EditText) findViewById(R.id.tests_field);
        cases_list = (ListView) findViewById(R.id.cases_list);
        create_button = (Button) findViewById(R.id.create_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        cases_list.setAdapter(adapter);
        get_cases();
        get_click();
    }

    /**
     * listener del botón y menú.
     */
    private void get_click() {
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_new_case_info();
                clear_fields();
                Toast.makeText(getApplicationContext(), "Caso creado", Toast.LENGTH_SHORT).show();
            }
        });

        cases_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                final String case_selected = (String)parent.getItemAtPosition(position);
                PopupMenu popup_menu = new PopupMenu(CasesManagementActivity.this, view);
                popup_menu.getMenuInflater().inflate(R.menu.popup_menu, popup_menu.getMenu());
                popup_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Eliminar")){
                            show_delete_dialog((String)parent.getItemAtPosition(position));
                        }else if (item.getTitle().equals("Editar")){
                            get_case_details(case_selected);
                            show_edit_dialog(case_selected);
                        }else{
                            get_case_details(case_selected);
                            show_overview_dialog(case_selected);
                        }
                        return true;
                    }
                });
                popup_menu.show();
            }
        });
    }

    /**
     * Muestra un mensaje al eliminar un caso.
     * @param case_name el nombre del caso.
     */
    private void show_delete_dialog(final String case_name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Eliminar Caso Clínico");
        dialog.setMessage("¿Está seguro de que quiere eliminar " + case_name + " .La decisión es final.");

        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), case_name + " eliminado", Toast.LENGTH_SHORT).show();
                RequestManager.DELETE("cases/" + case_name, "{}");
                update();
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

    /**
     * Muetsra un dialogo para editar los atributos del caso.
     * @param case_name el nombre del caso.
     */
    private void show_edit_dialog(final String case_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Editar " + case_name);
        dialog.setMessage("Introduce el contenido, si son varias especificaciones sepárelos por coma.");

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText medication_field = new EditText(this);
        medication_field.setText(medication_detail);
        layout.addView(medication_field);

        final EditText tests_field = new EditText(this);
        tests_field.setText(tests_detail);
        layout.addView(tests_field);

        dialog.setView(layout);

        dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), case_name + " editado", Toast.LENGTH_SHORT).show();
                RequestManager.PUT("cases/" + case_name, JSONHandler.build_json_case(case_name, medication_field.getText().toString(), tests_field.getText().toString()));
                update();
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

    /**
     * Muestra los detalles del caso.
     * @param case_name el caso.
     */
    private void show_overview_dialog(final String case_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(case_name);
        dialog.setMessage("Medicación: " + medication_detail + "\n" + "Exámenes: " + tests_detail + "\n" + "Costo: " + price_detail);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Información del árbol", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                show_tree_case_info(case_name);
            }
        });
        dialog.show();
    }

    /**
     * Muestra la información del caso en el árbol.
     * @param case_name el caso.
     */
    private void show_tree_case_info(final String case_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(case_name);
        dialog.setMessage("Altura: " + height_detail + "\n" + "Profundidad: " + depth_detail + "\n" + "Camino: " + path_detail);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Limpia los campos de texto.
     */
    private void clear_fields() {
        name_field.getText().clear();
        medication_field.getText().clear();
        tests_field.getText().clear();
    }

    /**
     * Petición para crear un nuevo caso.
     */
    private void send_new_case_info(){
        Toast.makeText(getApplicationContext(), "Nuevo caso creado", Toast.LENGTH_SHORT).show();
        RequestManager.POST("cases/new_case", JSONHandler.build_json_case(name_field.getText().toString(), medication_field.getText().toString(), tests_field.getText().toString()));
        update();
    }

    /**
     * Petición para obtener la lista completa de casos.
     */
    private void get_cases() {
        RequestManager.GET("cases");
        RequestManager.wait_for_response(1000);
        process_list(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Petición para obtener detalles de un caso.
     * @param case_name el caso.
     */
    private void get_case_details(String case_name){
        RequestManager.GET("cases/" + case_name);
        RequestManager.wait_for_response(1000);
        JSONObject json_detail = JSONHandler.parse_clinic_case_details(RequestManager.GET_REQUEST_DATA());
        try {
            medication_detail = json_detail.getString("medication");
            tests_detail = json_detail.getString("tests");
            price_detail = json_detail.getString("cost");
            height_detail = json_detail.getString("height");
            depth_detail = json_detail.getString("depth");
            path_detail = json_detail.getString("path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Procesa la lista de casos.
     * @param json_list los casos en json.
     */
    private void process_list(String json_list){
        try {
            JSONObject list = new JSONObject(json_list);
            JSONArray array = list.getJSONArray("cases");

            for (int i = 0; i < array.length(); i++){
                adapter.add(array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la actividad.
     */
    private void update(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
