package com.theteam.taskz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectPlatformSync extends AppCompatActivity {

    private LinearLayout google_calendar_button,github_button,notion_button,jira_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.select_platform_sync_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        google_calendar_button = (LinearLayout) findViewById(R.id.google_calendar_button);
        github_button = (LinearLayout) findViewById(R.id.github_button);
        notion_button = (LinearLayout) findViewById(R.id.notion_button);
        jira_button = (LinearLayout) findViewById(R.id.jira_button);

        github_button.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SyncGithub.class));
        });

        notion_button.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SyncNotion.class));
        });


    }
}