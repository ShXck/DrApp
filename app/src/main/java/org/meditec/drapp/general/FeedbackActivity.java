package org.meditec.drapp.general;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.RequestManager;

public class FeedbackActivity extends AppCompatActivity {

    private ListView comments_list;
    private ArrayAdapter<String> comments_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        comments_list = (ListView)findViewById(R.id.comments_list);
        comments_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        comments_list.setAdapter(comments_adapter);

        get_comments();

    }

    private void get_comments() {
        RequestManager.GET(HomePageActivity.identifier + "/feedback");
        RequestManager.wait_for_response(500);
        process_list(RequestManager.GET_REQUEST_DATA());
    }

    private void process_list(String comments){

        try {
            JSONObject array = new JSONObject(comments);

            for (int i = 0; i < array.length(); i++){
                comments_adapter.add(array.getString(String.valueOf(i + 1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
