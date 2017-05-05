package org.meditec.drapp.network;


import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestManager {

    private static OkHttpClient client = new OkHttpClient();
    private static String recent_data;

    public static void POST(String parameter, String data){

        String URL =  "http://192.168.1.6:7500/MediTECServer/meditec/medics/" + parameter;

        try{
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, data);
            final Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Error", "request ->" + call);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SAVE_RESPONSE_DATA(response.body().string());
                    Log.i("Response", recent_data);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void DELETE(String parameter, String data){

        String URL =  "http://192.168.1.6:7500/MediTECServer/meditec/medics/" + parameter;

        try{
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, data);
            final Request request = new Request.Builder()
                    .url(URL)
                    .delete(body)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Error", "request ->" + call);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SAVE_RESPONSE_DATA(response.body().string());
                    Log.i("Response", GET_REQUEST_DATA());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void GET(String parameter){

        String URL =  "http://192.168.1.6:7500/MediTECServer/meditec/medics/" + parameter;

        Request request = new Request.Builder()
                .url(URL)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "request ->" + call);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SAVE_RESPONSE_DATA(response.body().string());
                Log.i("Response", recent_data);
            }
        });
    }

    public static void PUT(String parameter, String data){

        String URL =  "http://192.168.1.6:7500/MediTECServer/meditec/medics/" + parameter;

        try{
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, data);
            final Request request = new Request.Builder()
                    .url(URL)
                    .put(body)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Error", "request ->" + call);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SAVE_RESPONSE_DATA(response.body().string());
                    Log.i("Response", recent_data);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void SAVE_RESPONSE_DATA(String data){
        recent_data = data;
    }

    public static String GET_REQUEST_DATA(){
        return recent_data;
    }

    public static void wait_for_response(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
