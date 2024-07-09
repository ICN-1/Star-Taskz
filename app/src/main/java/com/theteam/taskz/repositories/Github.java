package com.theteam.taskz.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.ai.client.generativeai.type.Content;
import com.theteam.taskz.data.UserData;
import com.theteam.taskz.models.GithubAccount;
import com.theteam.taskz.models.GithubIssue;
import com.theteam.taskz.utilities.JsonConverter;
import com.theteam.taskz.view_models.LoadableButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Github {
    private static String endpoint = "https://api.github.com/";

    private static RequestQueue queue;

    public static void validateUserToken(String token, Context application_context, LoadableButton button){
        if(queue == null){
            queue = Volley.newRequestQueue(application_context);
        }
        final Response.Listener<JSONObject> responseListener = jsonObject -> {
            if(button!=null){
                button.stopLoading();
            }
            Log.v("API_RESPONSE", "Got github profile : "+jsonObject.toString());

            try {
                Toast.makeText(application_context,"Welcome "+ jsonObject.getString("login").toString(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            // When data is gotten, we save the data;

            try {
                UserData.saveGithubAccessToken(GithubAccount.fromJson(JsonConverter.convertToHashMap(jsonObject)),token, application_context);
            } catch (JSONException e) {
                Log.v("API_RESPONSE", e.toString());
            }

        };

        final Response.ErrorListener errorListener = volleyError -> {
            if(button!=null){
                button.stopLoading();
            }
          Log.v("API_RESPONSE", volleyError.toString());
        };

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                endpoint+"user",
                null,
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header = new HashMap<>();
                header.put("Authorization", "token "+token);
                header.put("Content-Type","application/json");
                return header;
            }
        };

        queue.add(request);

    }

    public static void listAllIssues(GithubAccount account, Context application_context){
        if(queue == null){
            queue = Volley.newRequestQueue(application_context);
        }

        final String url = endpoint + "issues";
        final HashMap<String, String> header = new HashMap<>();
        header.put("Accept","application/vnd.github+json");
        header.put("Authorization", "Bearer "+account.token);

        final int method = Request.Method.GET;

        final Response.Listener<JSONArray> response = jsonArray -> {
            Log.v("API_RESPONSE", jsonArray.toString());
            for (int i =0; i<jsonArray.length(); i++){
                try {
                    final JSONObject object = jsonArray.getJSONObject(i);
                    final HashMap<String,Object> map = new HashMap<>();

                    for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                        String key = it.next();

                        map.put(key, object.get(key));
                    }
                    map.put("id", object.getJSONObject("repository").get("full_name").toString().concat(object.get("id").toString()));

                    final GithubIssue issue = GithubIssue.fromRawJson(map);
                    Log.v("API_RESPONSE", issue.toJson().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener error = volleyError -> {
            Log.v("API_RESPONSE", volleyError.toString());
        };

        final JsonArrayRequest request = new JsonArrayRequest(
                method,
                url,
                null,
                response,
                error
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };

        queue.add(request);



    }

    public static Void hello(){

        return null;
    }
}
