package com.theteam.taskz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.data.AuthenticationDataHolder;
import com.theteam.taskz.data.ViewPagerDataHolder;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.services.ApiService;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EmailSection extends Fragment {

    private LoadableButton button;
    private TextInputFormField emailForm,passwordForm,confirmPasswordForm;
    private UnderlineTextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.email_section, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.loadable_button);
        textView = view.findViewById(R.id.back);
        emailForm = view.findViewById(R.id.email_form);
        passwordForm = view.findViewById(R.id.password_form);
        confirmPasswordForm = view.findViewById(R.id.confirm_password_form);
        if(AuthenticationDataHolder.email != null){
            emailForm.setText(AuthenticationDataHolder.email);
        }

        textView.setOnClickListener(view1 -> {
            ViewPagerDataHolder.viewPager.setCurrentItem(0, true);
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            disableAllInput();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                button.stopLoading();
                if(!validateEmail()){
                    showErrorMessage("Email Wrongly Formatted !!");
                    enableAllInput();
                    return;
                }
                if(!validatePassword()){
                    showErrorMessage("Password should be at least 8 digits");
                    enableAllInput();
                    return;
                }
                if(!passwordForm.getText().trim().equals(confirmPasswordForm.getText().trim())){
                    showErrorMessage("Passwords are not matching");
                    enableAllInput();
                    return;
                }
                enableAllInput();
                AuthenticationDataHolder.email = emailForm.getText().trim();
                AuthenticationDataHolder.password = passwordForm.getText().trim();

                createAccount();
            }, 2500);
        });
    }


    void disableAllInput(){
        emailForm.setEnabled(false);
        passwordForm.setEnabled(false);
        confirmPasswordForm.setEnabled(false);
    }
    void enableAllInput(){
        emailForm.setEnabled(true);
        passwordForm.setEnabled(true);
        confirmPasswordForm.setEnabled(true);
    }

    boolean validateEmail(){
        final String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, emailForm.getText());
    }
    boolean validatePassword(){

        return passwordForm.getText().length()>= 8;
    }
    void showErrorMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void createAccount(){
        ApiService apiService = new ApiService(requireActivity(), requireActivity().getLayoutInflater());
        apiService.createAccount();

    }

}
