package com.theteam.taskz.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UserModel {

    private HashMap<String,Object> json;

    private Context context;

    public UserModel(Context context){
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("userdata", "{}");
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
        json = gson.fromJson(jsonString, mapType);
    }

    public static void saveUserData(HashMap<String,Object> map, Context context){
        SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();

        preferences.edit().putString("userdata",gson.toJson(map, mapType)).apply();
    }
    public static void clearUserData(Context context){
        SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);

        preferences.edit().remove("userdata").apply();
    }

    public void setNeedsToSync(boolean sync){
        final HashMap<String,Object> userJson = (HashMap<String, Object>) json.clone();

        userJson.replace("sync", sync);
        saveUserData(userJson, context);
    }

    public boolean needsSync(){
        return (boolean) json.get("sync");

    }

    public boolean isExists(){
        return !json.isEmpty();
    }

    public String uid(){
        return String.valueOf((int) Double.parseDouble(json.get("id").toString()));
    }
    public String authToken(){
        return json.get("authToken").toString();
    }

    public Calendar tokenExpiration(){
        final String expirationDate = json.get("tokenExpiration").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Log.i("API", expirationDate);
            Log.e("API", sdf.format(calendar.getTime()));
            calendar.setTime(Objects.requireNonNull(sdf.parse(expirationDate)));

        }
        catch (Exception e){
            Log.e("API", Objects.requireNonNull(e.getMessage()));
        }
        return calendar;

    }
    public String firstName(){
        return json.get("firstName").toString();
    }
    public String lastName(){
        return json.get("lastName").toString();
    }
    public String email(){
        return json.get("email").toString();
    }
    public String password(){
        return json.get("password").toString();
    }

    public Calendar dob(){
        String time = json.get("dateOfBirth").toString();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(Objects.requireNonNull(sdf.parse(time)));
            calendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        } catch (Exception e) {
            Log.e("AI", Objects.requireNonNull(e.getMessage()));
        }
        return calendar;
    }
}
