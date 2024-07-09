package com.theteam.taskz.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotionTask extends TaskModel{
    private NotionTask(Map<String, Object> json) {
        super(json);
    }

    public static NotionTask fromRawJson(HashMap<String,Object> json){
        HashMap<String,Object> taskJson = new HashMap<>();


        taskJson.put("id", json.get("id"));
        taskJson.put("globalId", json.get("id"));
        taskJson.put("name", json.get("title").toString());
        taskJson.put("status", json.get("status").toString());
        taskJson.put("time", json.get("created_time"));
        taskJson.put("end", json.get("closed_at"));
        taskJson.put("description",json.get("body"));
        taskJson.put("category","uncategorized");

        final List<String> categories = new ArrayList<String>();
        categories.add("work");
        categories.add("study");
        categories.add("holiday");
        categories.add("personal");

        if(json.get("labels") != null){
            final ArrayList<Map<String,Object>> labels = new Gson().fromJson(json.get("labels").toString(), new TypeToken<ArrayList<Map<String,Object>>>(){}.getType());
            for(final Map<String,Object> label : labels){
                if(categories.add(label.get("name").toString().toLowerCase())){
                    taskJson.replace("category",label);
                    break;
                }
            }
        }
        return new NotionTask(taskJson);
    }


}

