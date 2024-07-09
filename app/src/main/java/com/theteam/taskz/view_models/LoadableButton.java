package com.theteam.taskz.view_models;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.R;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class LoadableButton extends LinearLayout {
    private LottieAnimationView lottie;
    private LinearLayout root_layout;
    private TextView button_text;

    public LoadableButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attr){
        LayoutInflater.from(getContext()).inflate(R.layout.loadable_button, this, true);

        button_text = (TextView) findViewById(R.id.button_text);
        lottie = (LottieAnimationView) findViewById(R.id.loading_lottie);
        root_layout = (LinearLayout) findViewById(R.id.container);

        TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.LoadableButton);

        final boolean loading = typedArray.getBoolean(R.styleable.LoadableButton_loading, false);
        final String text = typedArray.getString(R.styleable.LoadableButton_text);
        final int textColor = typedArray.getColor(R.styleable.LoadableButton_textColor, getContext().getResources().getColor(R.color.white));
        final int buttonColor = typedArray.getColor(R.styleable.LoadableButton_backgroundColor, getContext().getResources().getColor(R.color.themeColor));

        button_text.setText(text);
        button_text.setTextColor(textColor);
        root_layout.setBackgroundTintList(ColorStateList.valueOf(buttonColor));

        // Although by default the button is not loading
        if(loading){
            startLoading();
        }

        typedArray.recycle();

    }



    public void setText(String text){
        button_text.setText(text);
    }
    /// To make the button start loading
    public void startLoading(){
        button_text.setVisibility(View.GONE);
        lottie.setVisibility(View.VISIBLE);
    }

    /// To make the button start loading

    public void stopLoading(){
        button_text.setVisibility(View.VISIBLE);
        lottie.setVisibility(View.GONE);
    }
}
