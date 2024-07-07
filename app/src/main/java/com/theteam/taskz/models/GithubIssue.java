package com.theteam.taskz.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubIssue extends TaskModel{
    private GithubIssue(Map<String, Object> json) {
        super(json);
    }

    public static GithubIssue fromRawJson(HashMap<String,Object> json){
        HashMap<String,Object> issueJson = new HashMap<>();

        issueJson.put("id", json.get("id"));
        issueJson.put("globalId", json.get("id"));
        issueJson.put("name", json.get("title").toString());
        issueJson.put("status", json.get("state").toString().equalsIgnoreCase("open")?"pending":"completed");
        issueJson.put("time", json.get("created_at"));
        issueJson.put("end", json.get("closed_at"));
        issueJson.put("description",json.get("body"));
        issueJson.put("category","uncategorized");

        final List<String> categories = new ArrayList<String>();
        categories.add("work");
        categories.add("study");
        categories.add("holiday");
        categories.add("personal");

        if(json.get("labels") != null){
            final ArrayList<Map<String,Object>> labels = new Gson().fromJson(json.get("labels").toString(), new TypeToken<ArrayList<Map<String,Object>>>(){}.getType());
            for(final Map<String,Object> label : labels){
                if(categories.add(label.get("name").toString().toLowerCase())){
                    issueJson.replace("category",label);
                    break;
                }
            }
        }
        return new GithubIssue(issueJson);
    }


}
