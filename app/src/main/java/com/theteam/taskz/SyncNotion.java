package com.theteam.taskz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.data.UserData;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.repositories.Notion;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class SyncNotion extends AppCompatActivity {

    private LinearLayout notion_sign_in;
    private TextInputFormField form;

    private LoadableButton button;
    private final int NOTION_LOGIN = 2010;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sync_notion_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notion_sign_in = (LinearLayout) findViewById(R.id.notion_sign_in_button);
        form = (TextInputFormField) findViewById(R.id.token_form);
        button = (LoadableButton) findViewById(R.id.loadable_button);

        notion_sign_in.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), NotionWebView.class);
            startActivityForResult(i,NOTION_LOGIN);
        });

        button.setOnClickListener(view -> {
            button.startLoading();
            new Handler().postDelayed(() -> {
                if(form.getText().trim().isEmpty()){
                    button.stopLoading();
                    return;
                }
                sync(form.getText().trim().toString());
            }, 2500);
        });


    }

    private void sync(final String database_url){
        String databse_id = database_url;
        // First when syncing we have to watch out for the Url.
        // It can come in the direct form:
        // https://www.notion.so/your-workspace/Database-Name-${databaseId}
        // Or in the jam form:
        // https://www.notion.so/${databaseId}?v=${random_nonsense}

        // To know if it is in jam form.
        if(database_url.contains("?v=")){
            databse_id = database_url.substring(database_url.lastIndexOf("/")+1, database_url.lastIndexOf("?v="));
            Log.v("Notion", databse_id);
        }
        // To know if it is in direct form.
        else if(database_url.contains("-")){
            databse_id = database_url.substring(database_url.lastIndexOf("-"));
        }

        Notion.listTasks(
                getApplicationContext(),
                databse_id,
                syncSuccess,
                syncError
        );

    }

    private Response.Listener<JSONObject> syncSuccess = response -> {
        button.stopLoading();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            Object obj = gson.fromJson(response.toString(), Object.class);
            Log.v("Notion", gson.toJson(obj));

        }
        catch (Exception e){
            e.printStackTrace();
            Log.v("Notion", response.toString());

        }

        try {
            final JSONArray tasksJson = response.getJSONArray("results");
            final ArrayList<TaskModel> tasks = new ArrayList<>();

            for(int i = 0; i < tasksJson.length(); i++){
                JSONObject taskJson = tasksJson.getJSONObject(i);
                final HashMap<String,Object> json = new HashMap<>();

                json.put("id",taskJson.getString("id"));
                json.put("globalId",taskJson.getString("id"));
                Calendar createdTime = Calendar.getInstance();
                createdTime.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(taskJson.getString("created_time")));

                json.put("time", taskJson.getString("created_time"));
                final JSONObject properties = taskJson.getJSONObject("properties");

                String endKey = null;
                for (Iterator<String> it = properties.keys(); it.hasNext(); ) {
                    String key = it.next();
                    if(properties.getJSONObject(key).optString("id","").contains("due_date")){
                        endKey = key;
                        break;
                    }
                }
                if(endKey != null){
                    final JSONObject endJson = properties.getJSONObject(endKey);
                    json.put("end", endJson.getJSONObject("date").getString("start"));
                }

                String statusKey = null;
                for (Iterator<String> it = properties.keys(); it.hasNext(); ) {
                    String key = it.next();
                    if(properties.getJSONObject(key).optString("id","").contains("status_property")||
                            properties.getJSONObject(key).optString("type","").contains("status")){
                        statusKey = key;
                        break;
                    }
                }
                if(statusKey != null){
                    final JSONObject status = properties.getJSONObject(statusKey);
                    json.put("status", status.getJSONObject("status").getString("name").toLowerCase().contains("done")? "completed":"pending");
                }
                else{
                    json.put("status", "pending");
                }

                // Task Name
                String titleKey = null;
                for (Iterator<String> it = properties.keys(); it.hasNext(); ) {
                    String key = it.next();
                    if(properties.getJSONObject(key).optString("id", "").equals("title")){
                        titleKey = key;
                        break;
                    }
                }
                if(titleKey!=null){
                    final JSONObject titleJson = properties.getJSONObject(titleKey);
                    final JSONArray titleArray = titleJson.getJSONArray("title");

                    json.put("name", titleArray.length()<1? "No Title" : titleArray.getJSONObject(0).getJSONObject("text").getString("content"));

                }else{
                    json.put("name", "No Title");
                }

                json.put("category","work");

                // Task Description
                String descriptionKey = null;
                for (Iterator<String> it = properties.keys(); it.hasNext(); ) {
                    String key = it.next();
                    if(properties.getJSONObject(key).optString("id","").contains("description_property")||
                    properties.getJSONObject(key).optString("type","").contains("description")){
                        descriptionKey = key;
                        break;
                    }
                }
                if(descriptionKey != null){
                    final JSONObject descriptionJson = properties.getJSONObject(descriptionKey);
                    final JSONArray descriptionArray = descriptionJson.getJSONArray("rich_text");

                    if(descriptionArray.length()>0){
                        json.put("description", descriptionArray.getJSONObject(0).getJSONObject("text").getString("content"));
                    }
                }


                tasks.add(new TaskModel(json));
            }
            Log.v("Notion", new Gson().toJson(tasks, new TypeToken<ArrayList<TaskModel>>(){}.getType()));

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    };
    private Response.ErrorListener syncError = error -> {
        button.stopLoading();
        Log.e("Notion", error.toString());
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NOTION_LOGIN && resultCode == RESULT_OK && data!=null && data.hasExtra("token")){
            Toast.makeText(getApplicationContext(), "Welcome " + Objects.requireNonNull(UserData.notionAccount(getApplicationContext())).user_name, Toast.LENGTH_SHORT).show();
        }
    }
}