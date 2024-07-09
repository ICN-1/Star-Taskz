package com.theteam.taskz.view_models;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.theteam.taskz.R;

public class SelectableButton extends LinearLayout {
    private ImageView prefixIcon;

    //boolean value to know if password is currently visible (readable) or not
    private boolean isSelected = false;
    private TextView label;

    private LinearLayout root_layout;

    private Drawable prefixDrawable;

    public SelectableButton(Context context) {
        super(context);
        init(null);
    }

    public SelectableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    private void init(AttributeSet attr){

        LayoutInflater.from(getContext()).inflate(R.layout.selectable_button, this, true);


        // Find views within the layout and configure theme

        prefixIcon = (ImageView) findViewById(R.id.prefix_icon);
        root_layout = (LinearLayout) findViewById(R.id.root_layout);
        label = (TextView) findViewById(R.id.text_view);

        // That is, if there's need to initialize views
        if(attr != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.SelectableButton);

            prefixDrawable = typedArray.getDrawable(R.styleable.SelectableButton_prefix);
            final boolean selected = typedArray.getBoolean(R.styleable.SelectableButton_isSelected, false);
            final String text = typedArray.getString(R.styleable.SelectableButton_label);


            label.setText(text);
            if(prefixDrawable == null){
                prefixIcon.setVisibility(View.GONE);
            }
            else{
                prefixIcon.setImageDrawable(prefixDrawable);
            }
            root_layout.setBackgroundResource(selected?R.drawable.selectable_button_selected_background:R.drawable.selectable_button_unselected_background);

            typedArray.recycle();




        }
    }



    public void select(final boolean selected){
        root_layout.setBackgroundResource(selected?R.drawable.selectable_button_selected_background:R.drawable.selectable_button_unselected_background);
    }


}
