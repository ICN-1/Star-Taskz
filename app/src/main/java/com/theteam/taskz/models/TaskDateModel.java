package com.theteam.taskz.models;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskDateModel {
    private Calendar calendar;

    public TaskDateModel(Calendar calendar) {
        this.calendar = calendar;
    }

    public String day(){
        return calendar.get(Calendar.DAY_OF_MONTH) + "";
    }

    public boolean isToday(){
        return calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    public String dayOfWeek(){
        return new SimpleDateFormat("EEE").format(calendar.getTime());
    }
}
