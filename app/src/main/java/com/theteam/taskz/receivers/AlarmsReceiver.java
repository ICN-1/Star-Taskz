package com.theteam.taskz.receivers;

import static com.theteam.taskz.utilities.AlarmManager.NOTIF_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.HomeActivity;
import com.theteam.taskz.R;
import com.theteam.taskz.TaskReminder;
import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.utilities.AlarmManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class AlarmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        HashMap<String,Object> taskJson = new Gson().fromJson(bundle.getString("TASK"), new TypeToken<HashMap<String,Object>>(){}.getType());

        final TaskModel model = new TaskModel(taskJson);
        final SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        final String timeString = format.format(model.date.getTime()).toUpperCase();

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.taskz_round);
        NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Uri soundUri = Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.notification);



        NotificationChannel taskChannel = new NotificationChannel(model.id, "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Reminder Channel");
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build();

        taskChannel.setSound(soundUri, audioAttributes);
        nm.createNotificationChannel(taskChannel);

        Intent i = new Intent(context.getApplicationContext(), HomeActivity.class);
        PendingIntent bubbleIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,i,PendingIntent.FLAG_MUTABLE, bundle);



        final String desc = "com.theteam.taskz.STAR_REMINDER";
        Person chatPerson = new Person.Builder()
                .setName("Star✨")
                .setIcon(IconCompat.createWithResource(context.getApplicationContext(), R.drawable.star))
                .setImportant(true)
                .build();

        Intent shortcutIntent = new Intent(context.getApplicationContext(),HomeActivity.class);
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.setAction(Intent.ACTION_DEFAULT);

        ShortcutInfoCompat shortcut =  new ShortcutInfoCompat.Builder(context.getApplicationContext(), "STAR_REMINDER")
                .setCategories(Collections.singleton(desc))
                .setIcon(chatPerson.getIcon())
                .setIntent(shortcutIntent)
                .setLongLived(true)
                .setShortLabel("Star Reminder✨")
                .build();
        ShortcutManagerCompat.pushDynamicShortcut(context.getApplicationContext(), shortcut);


        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(chatPerson)
                .addMessage("Your task '" + model.name + "' should start now 😊", model.date.getTimeInMillis(), chatPerson);

        NotificationCompat.BubbleMetadata bubbleData = new NotificationCompat.BubbleMetadata.Builder(bubbleIntent,
                IconCompat.createWithResource(context.getApplicationContext(), R.drawable.star))
                .setIcon(IconCompat.createWithResource(context.getApplicationContext(), R.drawable.star))
                .setDesiredHeight(600)
                .setIntent(bubbleIntent)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build();

        Context application_context = context.getApplicationContext();
        Bundle b = (Bundle) intent.getExtras().clone();
        Intent seenI = new Intent(context, TaskNotifReceiver.class);
        seenI.setAction(TaskNotifReceiver.ACTION_PENDING);
        seenI.putExtras(b);
        PendingIntent seenPI = PendingIntent.getBroadcast(application_context,model.notifIdExists?model.notifId*100:NOTIF_ID*100,seenI, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);


        Intent completedI = new Intent(context, TaskNotifReceiver.class);
        completedI.setAction(TaskNotifReceiver.ACTION_COMPLETED);
        completedI.putExtras(b);
        PendingIntent completedPI= PendingIntent.getBroadcast(application_context,model.notifIdExists?model.notifId*100:NOTIF_ID*100,completedI, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        Uri mUri = Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.energize_your_day);
        StateHolder.mediaPlayer = MediaPlayer.create(application_context, mUri);
        StateHolder.mediaPlayer.setVolume(1f, 1f);
        StateHolder.mediaPlayer.seekTo(24000);
        StateHolder.mediaPlayer.setLooping(true);
        StateHolder.mediaPlayer.start();


        Notification notif = new NotificationCompat.Builder(context.getApplicationContext(), model.id)
                .setSmallIcon(R.drawable.task)
                .setContentIntent(bubbleIntent)
                .setShortcutId("STAR_REMINDER")
                .addPerson(chatPerson)
                .setBubbleMetadata(bubbleData)
                .setStyle(messagingStyle)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(soundUri)
                .setChannelId(model.id)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.themeColor))
                .addAction(R.drawable.pause, "Seen", seenPI)
                .addAction(R.drawable.check, "Completed", completedPI)
                .build();
        nm.notify(model.notifIdExists? model.notifId: NOTIF_ID, notif);


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//
//        }
//        else{
//            Notification notif = new NotificationCompat.Builder(context.getApplicationContext(), model.id)
//                    .setContentTitle(model.name)
//                    .setContentText("Should start now, It's " + timeString)
//                    .setSmallIcon(R.drawable.task)
//                    .setLargeIcon(largeIcon)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setOngoing(false)
//                    .setSound(soundUri)
//                    .setChannelId(model.id)
//                    .setColor(context.getApplicationContext().getResources().getColor(R.color.themeColor))
//                    .setAutoCancel(true)
//                    .build();
//            nm.notify(model.notifIdExists? model.notifId:AlarmManager.NOTIF_ID, notif);
//        }

        if(!model.notifIdExists){
           NOTIF_ID++;
        }



        if(AlarmManager.speech == null){
            AlarmManager.speech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i == TextToSpeech.SUCCESS){
//                        final int res = AlarmManager.speech.setLanguage(new Locale("en", "US"));
//                        AlarmManager.speech.setPitch(0.3f);
                        AlarmManager.speech.setSpeechRate(1.5f);
                        AlarmManager.speech.speak("This is a Reminder From Star Tasks", TextToSpeech.QUEUE_ADD, null, null);
                        AlarmManager.speech.speak("Your Task '" + model.name + "' should start now.", TextToSpeech.QUEUE_ADD, null, null);
                        AlarmManager.speech.speak("It is " + timeString, TextToSpeech.QUEUE_ADD, null, null);
                        AlarmManager.speech.speak("Please, Do not forget your Task", TextToSpeech.QUEUE_ADD, null, null);



                    }
                }
            });
        }
        else{
            AlarmManager.speech.speak("This is a Reminder From Star,", TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("Your Task '" + model.name + "' in Star Tasks should start now.", TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("It is " + timeString, TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("Please, Do not forget your Task", TextToSpeech.QUEUE_ADD, null, null);
        }

    }

}