package com.theteam.taskz;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    boolean isDarkChecked, isSystemChecked;
    int systemTheme;
    SwitchCompat dark_mode_switch;
    SharedPreferences themePreferences;
    SharedPreferences.Editor editThemePreference;
    UiModeManager uiModeManager;
    CardView goToEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dark_mode_switch = findViewById(R.id.dark_mode_switch);
        goToEditProfile = findViewById(R.id.goToEditProfile);

        goToEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, EditProfile.class));
            }
        });

        uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        systemTheme = uiModeManager.getNightMode();
        if (systemTheme == UiModeManager.MODE_NIGHT_NO){
            isSystemChecked = false;
        }else if (systemTheme == UiModeManager.MODE_NIGHT_YES){
            isSystemChecked = true;
        }

        themePreferences = getSharedPreferences("App_Theme", MODE_PRIVATE);
        isDarkChecked = themePreferences.getBoolean("isDark", isSystemChecked);
        editThemePreference = themePreferences.edit();
        isDarkMode(isDarkChecked);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dark_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDarkMode(isChecked);
                editThemePreference.putBoolean("isDark", isChecked).commit();
            }
        });
    }

    private void isDarkMode(boolean isChecked){
        if (isChecked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dark_mode_switch.setChecked(true);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            dark_mode_switch.setChecked(false);
        }
    }
}