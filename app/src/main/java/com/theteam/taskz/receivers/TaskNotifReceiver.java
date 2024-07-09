package com.theteam.taskz.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.enums.TaskStatus;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.utilities.AlarmManager;

import java.util.HashMap;

public class TaskNotifReceiver extends BroadcastReceiver {

    public static String ACTION_COMPLETED = "com.theteam.taskz.COMPLETED";
    public static String ACTION_PENDING = "com.theteam.taskz.PENDING";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        final HashMap<String,Object> taskJson = new Gson().fromJson(bundle.getString("TASK"), new TypeToken<HashMap<String,Object>>(){}.getType());
        TaskModel model = new TaskModel(taskJson);
        NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(model.notifIdExists? model.notifId: AlarmManager.NOTIF_ID);

        if(StateHolder.mediaPlayer != null){
            StateHolder.mediaPlayer.stop();
        }

        final TaskManager manager = new TaskManager(context.getApplicationContext());
        model.updateStatus(intent.getAction().equalsIgnoreCase(ACTION_PENDING)?TaskStatus.Pending:TaskStatus.Completed);
        manager.updateTask(model);
        AlarmManager alarm = new AlarmManager(context,context.getApplicationContext());
        alarm.cancelAlarm(model);
        alarm.cancelAlarm(model);




    }
}