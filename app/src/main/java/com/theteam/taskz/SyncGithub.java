package com.theteam.taskz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theteam.taskz.data.UserData;
import com.theteam.taskz.repositories.Github;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;

public class SyncGithub extends AppCompatActivity {

    public static int GITHUB_LOGIN = 2024;
    private TextInputFormField token_form;

    private TextView textView;
    private final String client_id = "Ov23liNeRJLQcSordD0M";
    private final String client_secrets = "8b7c03cea5ab5bc357fa7133802900caf385bf91";

    private LinearLayout github_sign_in;
    private LoadableButton sync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sync_github_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for testing purposes
        if(UserData.githubAccount(getApplicationContext()) != null){
            Log.v("API_RESPONSE", "Github data is not null");
            Github.listAllIssues(UserData.githubAccount(getApplicationContext()), getApplicationContext());
        }
        else{
            Log.v("API_RESPONSE", "Github data is null");

        }

        token_form = (TextInputFormField) findViewById(R.id.token_form);
        textView = findViewById(R.id.issues);
        sync = (LoadableButton) findViewById(R.id.loadable_button);
        github_sign_in = (LinearLayout) findViewById(R.id.github_sign_in_button);

        textView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), GithubSyncDircetions.class));
        });


        sync.setOnClickListener(view -> {
           sync();
        });

        github_sign_in.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), GithubWebView.class);
            startActivityForResult(i,GITHUB_LOGIN);
        });

    }
    void sync(){
        sync.startLoading();
        new Handler().postDelayed(() -> {
            Github.validateUserToken(token_form.getText().trim(), getApplicationContext(),sync);
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the github login was successful,
        // We set the text of the form_field to the token, then we run things again
        if(requestCode == GITHUB_LOGIN && resultCode == RESULT_OK && data != null){
            token_form.setText(data.getStringExtra("token").toString());
            sync();
        }
    }
}