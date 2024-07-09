package com.theteam.taskz.models;

import com.google.gson.Gson;

import java.util.HashMap;

public class NotionAccount {
    public String access_token;
    public String bot_id;
    public String workspace_id,workspace_name,workspace_icon;

    public String user_id, user_name, user_icon,user_email;


    private NotionAccount(String access_token, String bot_id, String workspace_id, String workspace_name, String workspace_icon, String user_id, String user_name, String user_icon, String user_email) {
        this.access_token = access_token;
        this.bot_id = bot_id;
        this.workspace_id = workspace_id;
        this.workspace_name = workspace_name;
        this.workspace_icon = workspace_icon;
        this.user_email = user_email;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_icon = user_icon;
    }

    public static NotionAccount fromJson(String json) {
        return new Gson().fromJson(json, NotionAccount.class);
    }

    public HashMap<String,Object> toJson(){
        final HashMap<String,Object> map = new HashMap<>();
        map.put("access_token",access_token);
        map.put("bot_id",bot_id);
        map.put("workspace_id",workspace_id);
        map.put("workspace_name",workspace_name);
        map.put("workspace_icon",workspace_icon);
        map.put("user_id",user_id);
        map.put("user_name",user_name);
        map.put("user_icon",user_icon);
        map.put("user_email",user_email);
        return map;
    }
}
