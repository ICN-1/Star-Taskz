package com.theteam.taskz.state;

import android.view.View;

import com.theteam.taskz.adapters.TaskDateListAdapter;

import java.util.Calendar;

public class TasksState {

    public static TaskDateListAdapter.TaskDateViewHolder selectedView = null;
    public static TaskDateListAdapter.TaskDateViewHolder todayView = null;

    public static Calendar selectedDate;


}
