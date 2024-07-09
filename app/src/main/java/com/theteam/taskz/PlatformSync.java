package com.theteam.taskz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theteam.taskz.view_models.LoadableButton;

public class PlatformSync extends AppCompatActivity {

    private LoadableButton get_started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.platform_sync_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        get_started = (LoadableButton) findViewById(R.id.get_started);


        get_started.setOnClickListener(view -> {
            get_started.startLoading();

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), SelectPlatformSync.class);
                startActivity(intent);
                get_started.stopLoading();
            },3000);
        });
    }
}