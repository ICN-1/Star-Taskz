package com.theteam.taskz.models;

import java.util.HashMap;
import java.util.Map;

public class GithubAccount {
    public String username,name,email,link,bio,token,pic;

    private HashMap<String,Object> json;

    private GithubAccount(HashMap<String,Object> json){
        // This isn't in the initial json data returned
        // In Github's endpoint, But it is manually added
        if(json.get("token") != null){
            token = json.get("token").toString();
        }
        username = json.get("login").toString();
        name = json.get("name").toString();
        if(json.get("email") != null){
            email = json.get("email").toString();
        }
        if(json.get("bio") != null){
            bio = json.get("bio").toString();
        }
        link = json.get("url").toString();
        pic = json.get("avatar_url").toString();

        this.json = json;
    }

    public static GithubAccount fromJson(HashMap<String,Object> json){

        return new GithubAccount(json);
    }

    public HashMap<String,Object> toJson(){
        return json;
    }
}
