package org.meditec.drapp.general;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.meditec.drapp.R;
import org.meditec.drapp.network.JSONHandler;
import org.meditec.drapp.network.RequestManager;

public class HomePageActivity extends AppCompatActivity {

    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~:" +
            "(email-address,formatted-name,phone-numbers,picture-urls::(original))";

    private ProgressDialog progress;
    private TextView user_name, user_email;
    private ImageView profile_picture;
    private Button menu_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        // Initialize the progressbar
        progress= new ProgressDialog(this);
        progress.setMessage("Retrieving data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        user_email = (TextView) findViewById(R.id.email);
        user_name = (TextView) findViewById(R.id.name);
        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        menu_button = (Button)findViewById(R.id.main_button);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_main_menu_screen();
            }
        });

        linkedInApiHelper();

    }

    private void get_main_menu_screen() {
        Intent main_menu = new Intent(HomePageActivity.this, MainMenuActivity.class);
        startActivity(main_menu);
    }

    public void linkedInApiHelper(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(HomePageActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    showResult(result.getResponseDataAsJson());
                    RequestManager.POST("login", JSONHandler.get_json_med_info(user_name.getText().toString(), user_email.getText().toString()));
                    progress.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError error) {

            }
        });
    }

    public  void  showResult(JSONObject response){

        try {
            user_email.setText(response.get("emailAddress").toString());
            user_name.setText(response.get("formattedName").toString());

            Picasso.with(this).load(response.getString("pictureUrl"))
                    .into(profile_picture);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
