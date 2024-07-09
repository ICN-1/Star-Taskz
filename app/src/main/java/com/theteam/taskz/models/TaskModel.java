package com.theteam.taskz.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.enums.TaskStatus;
import com.theteam.taskz.utilities.AlarmManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskModel {
    public Calendar date;
    public String name;
    public String id;
    public String globalId;
    public String description;
    public Calendar endDate;

    public int notifId = 0;
    public boolean notifIdExists = false;
    public String category;

    public TaskStatus status;


    public TaskModel(Map<String, Object> json){
        // If the task has an end date or time, we assign it
        if(json.get("end") != null){
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(parseStringToTime(json.get("end").toString()));
            endDate = endCalendar;
        }

        // We assign the start date and time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(parseStringToTime(json.get("time").toString()));
        date = calendar;

        // We assign the id
        id = json.get("id").toString();

        //The global id can be the same as the id
        //So when parsing, it may give a NumberFormatException because it'll be like : '#TASK-?'
        //So this will help us determine wether we should parse it or
        //... Leave it(Derived from 'id')
        try {
            globalId = String.valueOf((int) Double.parseDouble(json.get("globalId").toString()));
        }
        catch (NumberFormatException e){
            globalId = json.get("globalId").toString();

        }

        // We assign the name and category
        name = json.get("name").toString();
        category = json.get("category").toString();

        // If the task contains a description
        if(json.get("description") != null){
            description = json.get("description").toString();

        }

        // If the task has been scheduled to ring,
        // It will have a notifIf, so we assign it as well
        if(json.containsKey("notifId")){
            notifId = (int) Integer.parseInt(String.valueOf(json.get("notifId").toString()));
            notifIdExists=true;
        }
        if(json.get("status") == null){
            status = TaskStatus.Pending;
        }
        else{
            switch (json.get("status").toString().toLowerCase()){
                case "completed":
                    status = TaskStatus.Completed;
                    break;
                default:
                    status = TaskStatus.Pending;
            }
        }

    }

    //We create a factory constructor to decode json data given in different key-values from API
    public static TaskModel fromJsonObject(JSONObject object){


        final Type type = new TypeToken<HashMap<String,Object>>(){}.getType();
        final HashMap<String,Object> json = new HashMap<>();
        final Gson gson = new Gson();
        final HashMap<String,Object> objectMap = gson.fromJson(object.toString(), type);

        final String startDate = objectMap.get("startDate").toString();
        final String startTime = objectMap.get("startTime").toString();
        final String endDate = objectMap.get("endDate").toString();
        final String endTime = objectMap.get("endTime").toString();

        json.put("name", objectMap.get("taskName").toString());
        json.put("description", objectMap.get("taskDescription").toString());
        json.put("id", objectMap.get("fakeId").toString());
        json.put("globalId", objectMap.get("id").toString());
        json.put("status", objectMap.get("taskStatus")==null? "pending":objectMap.get("taskStatus").toString().toLowerCase());
        json.put("category", objectMap.get("taskCategory")==null? "Uncategorized":objectMap.get("taskCategory"));
        json.put("notifId", String.valueOf((int) AlarmManager.NOTIF_ID++));



        // To set the startTime and startDate;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(dateFormat.parse(startDate).getTime());
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(timeFormat.parse(startTime));
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        json.put("time", calendar.getTimeInMillis());


        // TODO: Do not forget to make both start and end dates and times the same if the ends aren't given.
        // If the task has an endDate or endTime
        Calendar endCalendar = Calendar.getInstance();
        if(!startTime.equalsIgnoreCase(endTime) || !endDate.equalsIgnoreCase(startDate)){
            try {
                endCalendar.setTimeInMillis(dateFormat.parse(endDate).getTime());
                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.setTime(timeFormat.parse(endTime));
                endCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                endCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
                endCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            json.put("end", endCalendar.getTimeInMillis());
        }

        return new TaskModel(json);

    }

    private static long parseStringToTime(String timeString) {
        try {
            // Try parsing without handling scientific notation
            return Long.parseLong(timeString);
        } catch (NumberFormatException e) {
            // If parsing fails, try parsing with scientific notation
            try {
                DecimalFormat df = new DecimalFormat("#");
                Number number = df.parse(timeString);
                return number.longValue();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return 0; // or throw an exception as needed
            }
        }
    }

    public void updateStatus(TaskStatus status){
        this.status = status;
    }


    public HashMap<String,Object> toJson(){
        HashMap<String,Object> json = new HashMap<>();
        json.put("id", id);
        json.put("name", name);
        json.put("category", category);
        if(notifIdExists){
            json.put("notifId", String.valueOf(notifId));
        }
        // If the globalId is the same as the id.
        // Then it means the globalId the globalId(Task id from API) does not exists.
        json.put("globalId", globalId);
        json.put("time", date.getTimeInMillis());
        json.put("status", status.name());

        return json;
    }


    //Used as a direct converter to the body format to be posted on the API
    public JSONObject toJsonObject() throws JSONException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());


        // Making a different calendar and setting the time
        // To a day more than the startDate and startTime.
        // Just to check if API error is due to the times being the same.

        Calendar endDate = (Calendar) this.date.clone();
        final int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
        final int year = date.get(Calendar.YEAR);
        boolean isLeapYear = date.get(Calendar.YEAR)%4 ==0;
        boolean isLastDay = isLeapYear? dayOfYear==365:dayOfYear==366;
        endDate.set(Calendar.DAY_OF_YEAR, isLastDay? 1: date.get(Calendar.DAY_OF_YEAR)+1);
        endDate.set(Calendar.YEAR, isLastDay? year+1:year);

        JSONObject jsonObject = new JSONObject();

        // I.e if the task has id gotten from API
        // The globalId and id cannot be the same because id from offline has '#TASK-' while the id from API is number.
        if(!globalId.equals(id)){
            jsonObject.put("id", Integer.parseInt(globalId));
        }

        jsonObject.put("taskName", name);
        jsonObject.put("taskDescription", name);
        jsonObject.put("startDate", dateFormat.format(date.getTime()));
        jsonObject.put("startTime", isoFormat.format(date.getTime()));
        jsonObject.put("endDate", dateFormat.format(endDate.getTime()));
        jsonObject.put("endTime", isoFormat.format(endDate.getTime()));
        jsonObject.put("taskStatus",status.name().toLowerCase());
        jsonObject.put("taskCategory", category.toLowerCase());


        return jsonObject;

    }
}
