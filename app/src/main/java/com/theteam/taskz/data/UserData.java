package com.theteam.taskz.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.models.GithubAccount;
import com.theteam.taskz.models.NotionAccount;

import java.lang.reflect.Type;
import java.util.HashMap;

public class UserData {

    private static final Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();

    public static void saveGithubAccessToken(GithubAccount githubAccount, String token, Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        HashMap<String, Object> json = githubAccount.toJson();
        json.put("token", token);
        Log.v("API_RESPONSE", token);

        preferences.edit().putString("Github_Account", gson.toJson(json)).apply();
    }

    public static void saveNotionAccessToken(NotionAccount account, Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);

        preferences.edit().putString("Notion_Account", new Gson().toJson(account.toJson())).apply();
    }

    public static void saveNotionAccessToken(HashMap<String, Object> account, Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);

        preferences.edit().putString("Notion_Account", new Gson().toJson(account)).apply();
    }

    public static void saveJiraAccessToken(String token, Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);

        preferences.edit().putString("Jira_Access_Token", token).apply();
    }

    public static void saveGoogleAccessToken(String token, Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);

        preferences.edit().putString("Google_Access_Token", token).apply();
    }

    public static GithubAccount githubAccount(Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);
        if(preferences.getString("Github_Account",null) == null){
            return null;
        }

        return GithubAccount.fromJson(new Gson().fromJson(preferences.getString("Github_Account",""),hashMapType));
    }
    public static String jiraToken(Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);

        return preferences.getString("Jira_Access_Token",null);
    }
    public static NotionAccount notionAccount(Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);
        if(preferences.getString("Notion_Account",null) == null){
            return null;
        }

        return NotionAccount.fromJson(preferences.getString("Notion_Account",""));
    }
    public static String googleToken(Context application_context){
        SharedPreferences preferences = application_context.getSharedPreferences("userData",Context.MODE_PRIVATE);

        return preferences.getString("Google_Access_Token",null);
    }




}
