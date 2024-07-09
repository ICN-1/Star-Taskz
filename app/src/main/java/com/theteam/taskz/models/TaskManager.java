package com.theteam.taskz.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.CalendarPageFragment;
import com.theteam.taskz.CreateTask;
import com.theteam.taskz.TasksPageFragment;
import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.enums.TaskStatus;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.services.ApiService;
import com.theteam.taskz.utilities.AlarmManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskManager {
    final public static ArrayList<TaskModel> tasks = new ArrayList<>();
    public static String END_POINT = "https://startaskzbackend-production.up.railway.app/";

    private Context context;

    private UserModel userModel;
    public TaskManager(Context context){
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);
        tasks.clear();
        for(final HashMap<String,Object> json : list) {
            if (!json.containsKey("id")) {
                json.put("id", "#TASK-" + list.indexOf(json));
            }
            tasks.add(new TaskModel(json));
        }

    }

    public void addTask(TaskModel model, boolean speak){
        userModel = new UserModel(context);
        tasks.add(model);
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);
        list.add(model.toJson());
        preferences.edit().putString("tasks", gson.toJson(list, new TypeToken<ArrayList<HashMap<String,Object>>>(){}.getType())).apply();

        refresh(model);

        if(model.id.equals(model.globalId)){
            //Now, We try to add the task in the cloud using the create-task endpoint.
            try {
                ApiService.addTask(context, model);
            } catch (JSONException e) {
                Log.e("API_RESPONSE", "JSON EXCEPTION");
                Log.e("API_RESPONSE", e.toString());
            }
        }
        if(Calendar.getInstance().getTime().before(model.date.getTime())){
            if(model.status != TaskStatus.Completed){
                final AlarmManager taskReminder = new AlarmManager(context.getApplicationContext(), context);
                taskReminder.setAlarm(model, speak);
            }

        }

    }

    public void deleteTask(TaskModel model){
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);

        int index = -1;
        for(int i = 0; i<getTasks().size(); i++){
            if(getTasks().get(i).id.equals(model.id)){
                index = i;
                break;
            }
        }

        if(index != -1){
            list.remove(index);
        }
        preferences.edit().putString("tasks", gson.toJson(list, new TypeToken<ArrayList<HashMap<String,Object>>>(){}.getType())).apply();
        refresh(model);

        if(Calendar.getInstance().getTime().before(model.date.getTime())){
            final AlarmManager taskReminder = new AlarmManager(context.getApplicationContext(), context);
            taskReminder.cancelAlarm(model);
        }

        ApiService.deleteTask(context, model);
    }
    public void clearTasks(){
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);

        new AlarmManager(context, context.getApplicationContext()).cancelAllAlarms();
        preferences.edit().putString("tasks", "[]").apply();

    }

    public void updateTask(TaskModel model){
        updateTaskOffline(model,false);

        try {
            ApiService.updateTask(context, model);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updateTask(TaskModel model, boolean speak){
        updateTaskOffline(model, speak);

        try {
            ApiService.updateTask(context, model);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updateTaskOffline(TaskModel model, boolean speak){
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<Map<String, Object>>>(){}.getType();
        ArrayList<Map<String, Object>> list = gson.fromJson(jsonString, listType);

        final HashMap<String,Object> maps = model.toJson();

        int index = 0;
        final List<TaskModel> _tasks = getTasks();
        for(final TaskModel _model: _tasks){
            if(Objects.equals(_model.id, model.id)){
                index = _tasks.indexOf(_model);
            }
        }

        for(String key: maps.keySet()) {
            if (list.get(index).containsKey(key)) {
                list.get(index).replace(key, maps.get(key));
            } else {
                list.get(index).put(key, maps.get(key));
            }
        }
        preferences.edit().putString("tasks", gson.toJson(list)).apply();


        refresh(model);
        final AlarmManager taskReminder = new AlarmManager(context.getApplicationContext(), context);


        if(Calendar.getInstance().getTime().before(model.date.getTime())){
            taskReminder.cancelAlarm(model);
            taskReminder.setAlarm(model, speak,false);
        }
        else{
            if(model.status == TaskStatus.Completed){
                taskReminder.cancelAlarm(model);
            }
        }

    }

    public ArrayList<TaskModel> getTasks(){
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);
        tasks.clear();
        for(final HashMap<String,Object> json : list){
            if(!json.containsKey("id")){
                json.put("id", "#TASK-" + list.indexOf(json));
            }
            tasks.add(new TaskModel(json));
        }
        return tasks;
    }

    private void refresh(TaskModel model){
        if(StateHolder.taskPageFragments!= null){
            //Inorder to automatically refresh the Page that needs this update,
            for(TasksPageFragment fragment: StateHolder.taskPageFragments){
                if(fragment.date.get(Calendar.MONTH) == model.date.get(Calendar.MONTH) && fragment.date.get(Calendar.DAY_OF_MONTH) == model.date.get(Calendar.DAY_OF_MONTH)){
                    // We refresh
                    // An error might be thrown if the fragment does not have an activity
                    // Because in the instantiateTasks() method, we are calling requireActivity()

                    try{
                        fragment.instantiateTasks();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
        if(StateHolder.calendarPageFragments != null){
            for(CalendarPageFragment fragment: StateHolder.calendarPageFragments){
                if(fragment.date.get(Calendar.MONTH) == model.date.get(Calendar.MONTH) && fragment.date.get(Calendar.DAY_OF_MONTH) == model.date.get(Calendar.DAY_OF_MONTH)){
                    //We refresh
                    // We refresh
                    // An error might be thrown if the fragment does not have an activity
                    // Because in the instantiateTasks() method, we are calling requireActivity()
                    try{
                        fragment.instantiateTasks();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
