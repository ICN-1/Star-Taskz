package com.theteam.taskz.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.receivers.AlarmsReceiver;
import com.theteam.taskz.R;
import com.theteam.taskz.models.TaskModel;

import java.util.Calendar;
import java.util.Locale;

public class AlarmManager {
    public static int NOTIF_ID = 0;
    static final int ALARM_REQ_CODE = 100;
    private static PendingIntent pi;

    public static TextToSpeech speech;

    private android.app.AlarmManager star_taskz;

    private long time;
    private Context application_context,activity_context;

    public AlarmManager(Context application_context,  Context activity_context){
        this.application_context = application_context;
        this.activity_context = activity_context;
        this.time = time;
    }




    public void setAlarm(TaskModel model, boolean speak) {
        Bundle b = new Bundle();
        b.putString("TASK", new Gson().toJson(model.toJson()));
        Intent intent = new Intent(application_context, AlarmsReceiver.class);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final Calendar calendar = (Calendar) model.date.clone();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        time = calendar.getTimeInMillis();
        pi = PendingIntent.getBroadcast(application_context, model.notifIdExists ? model.notifId : NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (star_taskz == null) {
            star_taskz = (android.app.AlarmManager) application_context.getSystemService(Context.ALARM_SERVICE);
        }

        try {
            star_taskz.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, time, pi);
            Log.v("ALARM", "SET EXACT ALARM");

        } catch (SecurityException e) {
            star_taskz.set(android.app.AlarmManager.RTC_WAKEUP, time, pi);
            Log.v("ALARM", e.getMessage());
        }


        final SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        final String timeString = format.format(model.date.getTime()).toUpperCase();

        Bitmap largeIcon = BitmapFactory.decodeResource(activity_context.getResources(), R.drawable.taskz_round);

        NotificationManager nm = (NotificationManager) application_context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel taskChannel = new NotificationChannel(model.id, "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Reminder Channel");
        Uri soundUri = Uri.parse("android.resource://" + application_context.getPackageName() + "/" + R.raw.notification);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build();

        taskChannel.setSound(soundUri, audioAttributes);
        nm.createNotificationChannel(taskChannel);

        Notification notif = new NotificationCompat.Builder(activity_context, model.id)
                .setContentTitle(model.name)
                .setContentText("Task would start by " + timeString)
                .setSmallIcon(R.drawable.task)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setOngoing(false)
                .setSound(soundUri)
                .setChannelId(model.id)
                .setColor(activity_context.getResources().getColor(R.color.themeColor))
                .setAutoCancel(true)
                .build();
        nm.notify(model.notifId, notif);
        NOTIF_ID++;

        String timeDistance = "now";
        if (isToday(model)) {
            timeDistance = getHourOrMinutesDistance(model);
        } else {
            timeDistance = getDaysDistance(model);
        }

        if (speak) {
            AlarmManager.speech.speak("Star speaking,", TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("Your new task would start " + timeDistance, TextToSpeech.QUEUE_ADD, null, null);
        }
    }
    public void setAlarm(TaskModel model, boolean speak, boolean notify) {
        Bundle b = new Bundle();
        b.putString("TASK", new Gson().toJson(model.toJson()));
        Intent intent = new Intent(application_context, AlarmsReceiver.class);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final Calendar calendar = (Calendar) model.date.clone();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        time = calendar.getTimeInMillis();
        pi = PendingIntent.getBroadcast(application_context, model.notifIdExists ? model.notifId : NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (star_taskz == null) {
            star_taskz = (android.app.AlarmManager) application_context.getSystemService(Context.ALARM_SERVICE);
        }

        try {
            star_taskz.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, time, pi);
            Log.v("ALARM", "SET EXACT ALARM");

        } catch (SecurityException e) {
            star_taskz.set(android.app.AlarmManager.RTC_WAKEUP, time, pi);
            Log.v("ALARM", e.getMessage());
        }


        final SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        final String timeString = format.format(model.date.getTime()).toUpperCase();

        Bitmap largeIcon = BitmapFactory.decodeResource(activity_context.getResources(), R.drawable.taskz_round);

        NotificationManager nm = (NotificationManager) application_context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel taskChannel = new NotificationChannel(model.id, "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Reminder Channel");
        Uri soundUri = Uri.parse("android.resource://" + application_context.getPackageName() + "/" + R.raw.notification);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build();

        taskChannel.setSound(soundUri, audioAttributes);
        nm.createNotificationChannel(taskChannel);

        if(notify){
            Notification notif = new NotificationCompat.Builder(activity_context, model.id)
                    .setContentTitle(model.name)
                    .setContentText("Task would start by " + timeString)
                    .setSmallIcon(R.drawable.task)
                    .setLargeIcon(largeIcon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true)
                    .setOngoing(false)
                    .setSound(soundUri)
                    .setChannelId(model.id)
                    .setColor(activity_context.getResources().getColor(R.color.themeColor))
                    .setAutoCancel(true)
                    .build();
            nm.notify(model.notifId, notif);
            NOTIF_ID++;
        }

        String timeDistance = "now";
        if (isToday(model)) {
            timeDistance = getHourOrMinutesDistance(model);
        } else {
            timeDistance = getDaysDistance(model);
        }

        if (speak) {
            AlarmManager.speech.speak("Star speaking,", TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("Your new task would start " + timeDistance, TextToSpeech.QUEUE_ADD, null, null);

        }
    }
    public void cancelAlarm(TaskModel model){
        Bundle b = new Bundle();
        b.putString("TASK", new Gson().toJson(model.toJson()));
        Intent intent = new Intent(activity_context, AlarmsReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtras(b);

        pi = PendingIntent.getBroadcast(application_context, model.notifIdExists?model.notifId:NOTIF_ID, intent, PendingIntent.FLAG_IMMUTABLE);

        if(star_taskz == null){
            star_taskz = (android.app.AlarmManager) activity_context.getSystemService(Context.ALARM_SERVICE);
        }

        star_taskz.cancel(pi);
    }
    public void cancelAllAlarms(){
        if(star_taskz == null){
            star_taskz = (android.app.AlarmManager) activity_context.getSystemService(Context.ALARM_SERVICE);
        }
        for(TaskModel model: new TaskManager(activity_context).getTasks()){
            cancelAlarm(model);
        }
        //further assurance to clear. But only on API 34 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            star_taskz.cancelAll();
        }
    }
    private boolean isToday(TaskModel model){
        return model.date.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }
    private boolean isSameHour(TaskModel model){
        return model.date.get(Calendar.HOUR_OF_DAY) == Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }
    private String getHourOrMinutesDistance(TaskModel model){
        String hoursGap = String.valueOf(model.date.get(Calendar.HOUR_OF_DAY)-Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        final String text2 = hoursGap + (hoursGap.equals("1") ? " hour":" hours");
        String minsGap = String.valueOf(model.date.get(Calendar.MINUTE)-Calendar.getInstance().get(Calendar.MINUTE));

        if(isSameHour(model)){
            return "in " + minsGap + (minsGap.equals("1")?" minute": " minutes");
        }
        return "in " + text2;
    }

    private String getDaysDistance(TaskModel model){
        final int dayDistance = model.date.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        String daysGap = String.valueOf(dayDistance);

        if(dayDistance==1){
            return "tomorrow";
        }
        return "in " + daysGap + "days";
    }




}
