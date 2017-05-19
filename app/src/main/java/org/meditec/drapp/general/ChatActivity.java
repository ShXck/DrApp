package org.meditec.drapp.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

public class ChatActivity extends AppCompatActivity {

    private ListView messages_list;
    private EditText message_field;
    private ArrayAdapter messages_list_adapter;
    private ImageButton send_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages_list = (ListView)findViewById(R.id.messages_list);
        messages_list_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        send_button = (ImageButton)findViewById(R.id.send_button);
        message_field = (EditText)findViewById(R.id.message_text);
        messages_list.setAdapter(messages_list_adapter);
        //get_available_medics();
        get_messages_list();
        set_listener();
    }

    private void set_listener() {
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestManager.POST("chat", JSONHandler.build_msg(message_field.getText().toString(), HomePageActivity.identifier));
                RequestManager.wait_for_response(1000);
                update();
            }
        });
    }

    private void get_messages_list() {
        RequestManager.GET("chat");
        RequestManager.wait_for_response(1000);
        process_messages_list(RequestManager.GET_REQUEST_DATA());
    }

    private void get_available_medics() {
        RequestManager.GET("medics");
        RequestManager.wait_for_response(1000);
        //process_medics_list(RequestManager.GET_REQUEST_DATA());
    }

    private void process_messages_list(String json_messages){
        try {
            JSONObject messages = new JSONObject(json_messages);
            JSONArray array = messages.getJSONArray("messages");

            for (int i = 0; i < array.length(); i++){
                messages_list_adapter.add(array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   /* private void process_medics_list(String list){
        try {
            JSONObject medics = new JSONObject(list);
            JSONArray array = medics.getJSONArray("medics");

            for(int i = 0; i < array.length(); i++){
                online_meds_adapter.add(array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void update(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
