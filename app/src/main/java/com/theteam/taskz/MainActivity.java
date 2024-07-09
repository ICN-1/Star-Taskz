package com.theteam.taskz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.utilities.AlarmManager;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.UnderlineTextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
public class MainActivity extends AppCompatActivity {

    private TextView app_name;
    private View lottie;


    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int REQUEST_DRAW_OVER_OTHER_APP_PERMISSION = 2;
    private static final int REQUEST_SCHEDULE_ALARMS = 3;





    private final String[] permissions = new String[]{
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.TURN_SCREEN_ON,
            Manifest.permission.INTERNET,
            Manifest.permission.SET_ALARM,
            Manifest.permission.WAKE_LOCK
    };

    private boolean permissionsGranted(){
        return checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(permissions[2]) == PackageManager.PERMISSION_GRANTED;
    }

    // Call this method from your activity or fragment
    private void requestNotificationPermission() {
        requestPermissions(permissions, REQUEST_NOTIFICATION_PERMISSION);
    }
    public boolean canDrawOverOtherApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }
    private void requestDrawOverOtherAppPermission(){
        if(canDrawOverOtherApps()){
            requestAlarmPermission();
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_DRAW_OVER_OTHER_APP_PERMISSION);
    }

    public boolean canScheduleAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true; // For Android versions below S, the permission is not needed
    }
    private void requestAlarmPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(canScheduleAlarms()){
                requestNotificationPermission();
                return;
            }
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivityForResult(intent, REQUEST_SCHEDULE_ALARMS);
        }
        else {
            initialize();
        }
    }



    // Override onRequestPermissionsResult to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            boolean permissionsGranted = false;
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    //Can post Notifications
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                    //Can record audio
            ){
                permissionsGranted = true;
            }
            initialize();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DRAW_OVER_OTHER_APP_PERMISSION) {
            requestAlarmPermission();
        }
        if(requestCode == REQUEST_SCHEDULE_ALARMS){
            requestNotificationPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app_name = (TextView) findViewById(R.id.app_name);
        lottie = (LottieAnimationView) findViewById(R.id.loading_lottie);

        Dialog dialog = new Dialog(this);
        View contentView = getLayoutInflater().inflate(R.layout.star_intro_dialog, null);
        final LottieAnimationView star_intro = (LottieAnimationView) contentView.findViewById(R.id.ai_lottie);
        final TextView content_title = (TextView) contentView.findViewById(R.id.content_title);
        final TextView content_text = (TextView) contentView.findViewById(R.id.content_text);

        final LoadableButton loadableButton = (LoadableButton) contentView.findViewById(R.id.go_button);
        final UnderlineTextView skipButton = (UnderlineTextView) contentView.findViewById(R.id.skip_button);

        star_intro.setAnimation(R.raw.star_happy);
        star_intro.playAnimation();

        content_title.setText("Hi!");
        content_text.setText("Inorder to have smooth experience using Star Taskz, We would need the following permissions enabled:\n\n◼ Appear On Screen\n◼ Alarm Management\n◼ Audio Recording\n◼ Post Notifications");
        loadableButton.setText("Enable Permissions >");
        skipButton.setText("Close");
        loadableButton.setOnClickListener(view -> {
            dialog.dismiss();
            if(!canDrawOverOtherApps()){
                requestDrawOverOtherAppPermission();
            }
            else{
                requestNotificationPermission();
            }
        });
        skipButton.setOnClickListener(view -> {
            initialize();
        });

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);

        if(canScheduleAlarms() && canDrawOverOtherApps() && permissionsGranted()){
            initialize();
        }
        else{
            dialog.show();
        }

    }

    void showMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }



    private void startAnimation(){
        final UserModel model = new UserModel(this);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(2000);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setDuration(2000);

                app_name.setAnimation(fadeOut);
                runOnUiThread(() -> {
                    hideAppName();
                    showLottie();
                });
                lottie.setAnimation(fadeIn);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(),model.isExists()? HomeActivity.class: LoginActivity.class);
                        startActivity(i);
                        cancel();
                        finish();
                    }
                }, 5000);
            }
        };

        new Timer().schedule(task, 5000);




    }

    private void initialize(){
        AlarmManager.speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
//                    AlarmManager.speech.setPitch(0.3f);
                    AlarmManager.speech.setSpeechRate(1.4f);

                }
                else{
                    AlarmManager.speech=null;

                }
            }
        });
        startAnimation();
    }

    void showLottie(){
        lottie.setVisibility(View.VISIBLE);

    }

    void hideAppName(){
        app_name.setVisibility(View.GONE);

    }
}