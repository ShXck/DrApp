package org.meditec.drapp.general;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

import java.util.ArrayList;

public class MedicTestsActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
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

        //adapter.clear();
        //tests_list.clear();

        list_view_tests = (ListView)findViewById(R.id.tests_list);
        name_field = (EditText)findViewById(R.id.name);
        price_field = (EditText)findViewById(R.id.price_field);
        create_button = (Button)findViewById(R.id.create_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        list_view_tests.setAdapter(adapter);
        get_tests();
        get_click();
    }

    /**
     * listener de l¿los botones.
     */
    private void get_click() {
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_test();
                clear_fields();
                update();
                Toast.makeText(getApplicationContext(), "Exámen creado", Toast.LENGTH_SHORT).show();
            }
        });

        list_view_tests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String test_chosen = (String)parent.getItemAtPosition(position);
                PopupMenu popup_menu = new PopupMenu(MedicTestsActivity.this, view);
                popup_menu.getMenuInflater().inflate(R.menu.popup_menu, popup_menu.getMenu());
                popup_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Eliminar")){
                            show_delete_dialog(test_chosen);
                        }else if (item.getTitle().equals("Editar")){
                            get_test_details(test_chosen);
                            show_edit_dialog(test_chosen);
                        }else{
                            get_test_details(test_chosen);
                            show_overview_dialog(test_chosen);
                        }
                        return true;
                    }
                });
                popup_menu.show();
            }
        });
    }

    /**
     * Petición para obtener los detalles de un examen.
     * @param test_name el nombre del exmamen.
     */
    private void get_test_details(String test_name) {
        RequestManager.GET("tests/" + test_name);
        RequestManager.wait_for_response(1000);
        JSONObject json_info = JSONHandler.parse(RequestManager.GET_REQUEST_DATA());
        try {
            name_detail = json_info.getString("name");
            cost_detail = json_info.getString("cost");
        }catch (JSONException j){
            j.printStackTrace();
        }

    }

    /**
     * muestra una advertencia
     * @param test_name el nombre del examen.
     */
    private void show_delete_dialog(final String test_name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Eliminar " + test_name);
        dialog.setMessage("¿Está seguro de que quiere eliminar " + test_name + " . La decisión es final.");

        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestManager.DELETE("tests/" + test_name, "{}");
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
     * Muestra el dialogo para editar las propiedades del exámen.
     * @param test_name el nombre del exámen.
     */
    private void show_edit_dialog(final String test_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Editar " + test_name);
        dialog.setMessage("Edita el precio y el nombre en los espacios");

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name_field = new EditText(this);
        name_field.setText(name_detail);
        layout.addView(name_field);

        final EditText cost_field = new EditText(this);
        cost_field.setText(cost_detail);
        layout.addView(cost_field);

        dialog.setView(layout);

        dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestManager.PUT("tests/" + test_name, JSONHandler.build_new_test(name_field.getText().toString(), cost_field.getText().toString()));
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
     * Muestra la información del exámen en un dialogo.
     * @param test_name el nombre del exámen.
     */
    private void show_overview_dialog(final String test_name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(test_name);
        dialog.setMessage("Name: " + name_detail + "\n" + "Costo total: " + cost_detail);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Petición para obtener la lista de exámenes.
     */
    private void get_tests() {
        RequestManager.GET("tests");
        RequestManager.wait_for_response(1000);
        process_list(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Crea un nuevo examen en el servidor.
     */
    private void create_test(){
        RequestManager.POST("tests/new_test", JSONHandler.build_new_test(name_field.getText().toString(), price_field.getText().toString()));
    }

    /**
     * Limpia los espacios de texto.
     */
    private void clear_fields(){
        name_field.getText().clear();
        price_field.getText().clear();
    }

    /**
     * Procesa la lista de examenes.
     * @param json_list la lista de examenes.
     */
    private void process_list(String json_list){
        try {
            JSONObject list = new JSONObject(json_list);
            JSONArray array = list.getJSONArray("tests");

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
