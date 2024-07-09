package com.theteam.taskz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GithubWebView extends AppCompatActivity {

    private static final String CLIENT_ID = "Ov23liNeRJLQcSordD0M";
    private final String CLIENT_SECRET = "8b7c03cea5ab5bc357fa7133802900caf385bf91";
    private static final String REDIRECT_URI = "https://star-taskz.vercel.app/app";
    private static final String AUTH_URL = "https://github.com/login/oauth/authorize" +
            "?client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI +
            "&scope=user";
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.webview_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to handle the redirect
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri.toString().startsWith(REDIRECT_URI)) {
                    handleRedirectUri(uri);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        // Load the GitHub authorization URL in the WebView
        webView.loadUrl(AUTH_URL);
    }
    @SuppressLint("SetTextI18n")
    private void handleRedirectUri(Uri uri) {
        String code = uri.getQueryParameter("code");
        if (code != null) {
            // Exchange the code for an access token
            exchangeCodeForToken(code);
        } else {
            // Handle error or deny access
            webView.loadData("<html><body>Authorization failed</body></html>", "text/html", "UTF-8");
        }
    }

    private void exchangeCodeForToken(String code) {
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("client_id", CLIENT_ID);
            jsonObject.put("client_secret", CLIENT_SECRET);
            jsonObject.put("code",code);
            jsonObject.put("redirect_url",REDIRECT_URI);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final Response.Listener<JSONObject> response = jsonObject1 -> {
            Log.v("API_RESPONSE", "Got token successfully");
            Log.v("API_RESPONSE", jsonObject1.toString());


            try {
                final Intent intent = new Intent();
                String accessToken = jsonObject1.getString("access_token");
                intent.putExtra("token",accessToken);
                setResult(RESULT_OK, intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        final Response.ErrorListener error = volleyError -> {
            Log.v("API_RESPONSE", volleyError.toString());
        };

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://github.com/login/oauth/access_token",
                jsonObject,
                response,
                error
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header = new HashMap<>();
                header.put("Accept", "application/json");
                return header;
            }
        };

        queue.add(request);



    }
}