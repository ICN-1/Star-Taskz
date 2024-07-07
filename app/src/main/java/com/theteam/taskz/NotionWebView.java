package com.theteam.taskz;

import static com.theteam.taskz.repositories.Notion.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
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
import com.google.gson.Gson;
import com.theteam.taskz.data.UserData;
import com.theteam.taskz.models.NotionAccount;
import com.theteam.taskz.repositories.Notion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class NotionWebView extends AppCompatActivity {

    private static final String CLIENT_ID = Notion.CLIENT_ID;
    private final String CLIENT_SECRET = Notion.CLIENT_SECRET;
    private static final String REDIRECT_URI = Notion.REDIRECT_URL;
    private static final String AUTH_URL = "https://api.notion.com/v1/oauth/authorize?" +
            "client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI +
            "&response_type=code";
    private WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
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
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);  // Enable DOM storage
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Pixel 3 XL) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Mobile Safari/537.36");

        // Set a WebViewClient to handle the redirect
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("API_RESPONSE", "Page finished loading: " + url);
                super.onPageFinished(view, url);
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("API_RESPONSE",error.toString());
                super.onReceivedError(view, request, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String uri = request.getUrl().toString();
                Log.v("API_RESPONSE","Seeing to override");
                Log.v("API_RESPONSE",uri);


                if (uri.startsWith("https://star-taskz.vercel.app")) {
                    handleRedirectUri(request.getUrl());
                    return true;
                }
                Log.v("API_RESPONSE","Did not override loading");

                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        // Load the Notion authorization URL in the WebView
        Log.v("API_RESPONSE","Loading: " + AUTH_URL);
        webView.loadUrl(AUTH_URL);
    }
    @SuppressLint("SetTextI18n")
    private void handleRedirectUri(Uri uri) {
        String code = uri.getQueryParameter("code");
        if (code != null) {
            Log.v("API_RESPONSE","Code: " + code);
            // Exchange the code for an access token
            exchangeCodeForToken(code);
        } else {
            // Handle error or deny access
            webView.loadData("<html><body>Authorization failed</body></html>", "text/html", "UTF-8");
        }
    }

    private void exchangeCodeForToken(String code) {

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        final int method = Request.Method.POST;
        final String endpoint = BASE_URL + "oauth/token";

        final Response.Listener<JSONObject> response = jsonObject -> {
            Log.v("NOTION", jsonObject.toString());


            try {
                final String token = jsonObject.getString("access_token");
                HashMap<String,Object> map = new HashMap<>();
                map.put("access_token",token);
                map.put("bot_id",jsonObject.getString("bot_id"));
                map.put("workspace_name", jsonObject.getString("workspace_name"));
                map.put("workspace_id", jsonObject.getString("workspace_id"));
                map.put("workspace_icon", jsonObject.get("workspace_icon"));


                final JSONObject owner = jsonObject.getJSONObject("owner");
                if(owner.getString("type").equalsIgnoreCase("user")){
                    final JSONObject userJson = owner.getJSONObject("user");
                    map.put("user_id", userJson.getString("id"));
                    map.put("user_name", userJson.getString("name"));
                    map.put("user_icon", userJson.get("avatar_url"));
                    if(userJson.getString("type").equalsIgnoreCase("person")){
                        final JSONObject person = userJson.getJSONObject("person");
                        map.put("email", person.getString("email"));
                    }
                }

                UserData.saveNotionAccessToken(map, getApplicationContext());


                final Intent intent = new Intent();
                intent.putExtra("token",token);
                setResult(RESULT_OK, intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        final Response.ErrorListener error = volleyError -> {
            Log.v("NOTION", volleyError.getMessage() != null? volleyError.getMessage() : volleyError.toString());
        };

        final JSONObject body = new JSONObject();
        try {
            body.put("code", code);
            body.put("grant_type","authorization_code");
            body.put("redirect_uri", "https://star-taskz.vercel.app");
            body.put("client_id",CLIENT_ID);
            body.put("client_secret", CLIENT_SECRET);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        final String endcodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        final HashMap<String,String> header = new HashMap<>();
        header.put("Authorization","Basic '" + endcodedCredentials  + "'");
        header.put("Content-Type", "application/json");
        header.put("Notion-Version", "2022-06-28");

        final JsonObjectRequest request  = new JsonObjectRequest(
                method,
                endpoint,
                body,
                response,
                error
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };

        queue.add(request);
    }
}