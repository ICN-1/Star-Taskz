package com.theteam.taskz.repositories;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.theteam.taskz.data.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notion {
    public static String BASE_URL = "https://api.notion.com/v1/";
    public static String CLIENT_SECRET = "secret_a2Bh0rIVFREevKO0OvxE2srK95e3Q01FNjzXB9P2Ybm";
    public static String CLIENT_ID = "c3a08b97-e770-4a96-a37c-a5c55d265c40";

    private static RequestQueue queue;

    public static String REDIRECT_URL = "https%3A%2F%2Fstar-taskz.vercel.app";
    public static String AUTH_URL = "https://api.notion.com/v1/oauth/authorize?client_id=c3a08b97-e770-4a96-a37c-a5c55d265c40&response_type=code&owner=user&redirect_uri=https%3A%2F%2Fstar-taskz.vercel.app";

    public static void listTasks(Context application_context, final String database_id, final Response.Listener<JSONObject> response, final  Response.ErrorListener error){
        final String endpoint = BASE_URL +"databases/"+database_id + "/query";

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                endpoint,
                null,
                response,
                error
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + UserData.notionAccount(application_context).access_token);
                headers.put("Notion-Version", "2022-06-28");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        if(queue == null){
            queue = Volley.newRequestQueue(application_context);
        }
        queue.add(request);
    }

}
