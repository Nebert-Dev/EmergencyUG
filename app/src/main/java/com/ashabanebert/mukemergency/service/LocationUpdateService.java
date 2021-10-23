package com.ashabanebert.mukemergency.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.ashabanebert.mukemergency.helper.NetworkSingleton;
import com.ashabanebert.mukemergency.model.LocationUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.ashabanebert.mukemergency.helper.Config.LAST_SEEN;

public class LocationUpdateService extends Service
{

    private Timer timer = new Timer();
    String u;
    Gson gson;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        u = intent.getStringExtra("u");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLocationFromServer(u);
            }
        }, 0, 3*1000);  //check every 3 seconds

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    private void getLocationFromServer(String u) {
        String url = LAST_SEEN+u;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int code = response.getInt("code");
                            if(code == 200){
                                JSONObject data = response.getJSONObject("location");
                                JsonParser jsonParser = new JsonParser();
                                JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());
                                LocationUpdate locationUpdate = gson.fromJson(gsonObject, LocationUpdate.class);
                                EventBus.getDefault().post(locationUpdate);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        jsonObjectRequest.setShouldCache(false);
        NetworkSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
