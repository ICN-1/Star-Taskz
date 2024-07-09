package com.theteam.taskz;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.enums.TaskStatus;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.utilities.AlarmManager;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CreateTask extends AppCompatActivity{

    private EditText taskName,taskDescription;
    private LinearLayout startDateLayout, startTimeLayout, endDateLayout, endTimeLayout;

    private LinearLayout workLayout, personalLayout, uncategorizedLayout,studyLayout;
    private LoadableButton button;
    private TextView startDateText,startTimeText,endDateText,endTimeText;

    private TextView workText,personalText,uncategorizedText,studyText;

    private String category = "uncategorized";
    private Calendar calendar = Calendar.getInstance();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startDateLayout = findViewById(R.id.start_date_layout);
        startTimeLayout = findViewById(R.id.start_time_layout);
        endDateLayout = findViewById(R.id.end_date_layout);
        endTimeLayout = findViewById(R.id.end_time_layout);
        startDateText = findViewById(R.id.start_date_text);
        startTimeText = findViewById(R.id.start_time_text);
        endDateText = findViewById(R.id.end_date_text);
        endTimeText = findViewById(R.id.end_time_text);
        taskName = findViewById(R.id.task_name);
        taskDescription = findViewById(R.id.task_description);
        workLayout = findViewById(R.id.work_category);
        personalLayout = findViewById(R.id.personal_category);
        uncategorizedLayout = findViewById(R.id.uncategorized_category);
        studyLayout = findViewById(R.id.study_category);
        workText = findViewById(R.id.work_text);
        personalText = findViewById(R.id.personal_text);
        uncategorizedText = findViewById(R.id.uncategorized_text);
        studyText = findViewById(R.id.study_text);

        findViewById(R.id.back).setOnClickListener(view -> {
            onBackPressed();
        });

        final String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
        startTimeText.setText(time.toUpperCase());

        startDateLayout.setOnClickListener(view -> {
            showDatePickerDialog(true);
        });
        startTimeLayout.setOnClickListener(view -> {
            showTimePickerDialog(true);
        });
        endDateLayout.setOnClickListener(view -> {
            showDatePickerDialog(false);
        });
        endTimeLayout.setOnClickListener(view -> {
            showTimePickerDialog(false);
        });

        workLayout.setOnClickListener(view -> {
            category = "work";
            refreshCategories();
        });
        personalLayout.setOnClickListener(view -> {
            category = "personal";
            refreshCategories();
        });
        uncategorizedLayout.setOnClickListener(view -> {
            category = "uncategorized";
            refreshCategories();
        });
        studyLayout.setOnClickListener(view -> {
            category = "study";
            refreshCategories();
        });

//        title_text = findViewById(R.id.title_text);
//        subtitle_text = findViewById(R.id.subtitle_text);
//        taskName = findViewById(R.id.task_name_form);
//        taskDate = findViewById(R.id.task_date_form);
//        taskTime = findViewById(R.id.task_time_form);
//        taskCategory = findViewById(R.id.task_category_form);
//        button = findViewById(R.id.loadable_button);

        // To check if user wants to edit a task:
        // This is done because 'data' is only passed when a user wants to edit a task.
//        if(getIntent().hasExtra("data")){
//            Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
//            HashMap<String,Object> map = new Gson().fromJson(getIntent().getStringExtra("data"), mapType);
//
//            TaskModel model = new TaskModel(map);
//            final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
//            final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
//
//            calendar = model.date;
//            taskName.setText(model.name);
//            taskTime.setText(timeFormat.format(calendar.getTime()));
//            taskDate.setText(dateFormat.format(calendar.getTime()));
//            taskCategory.setText(model.category);
//
//            title_text.setText("Edit your Task.");
//            subtitle_text.setText("Edit the following details");
//            button.setText("EDIT TASK");
//
//        }
//
//        taskDate.setOnClickListener(view -> {
//            showDatePickerDialog();
//        });
//        taskTime.setOnClickListener(view -> {
//            showTimePickerDialog();
//        });
//        taskCategory.setOnClickListener(view -> {
//            showCategoryDialog();
//        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                button.startLoading();
//                disableAll();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        button.stopLoading();
//                        enableAll();
//                        if(taskName.getText().trim().isEmpty()){
//                            showMessage("Task Name is Empty");
//                            return;
//                        }
//                        if(taskTime.getText().trim().isEmpty()){
//                            showMessage("Time of task is Empty");
//                            return;
//                        }
//                        if(taskCategory.getText().trim().isEmpty()){
//                            showMessage("Task Category is Empty");
//                            return;
//                        }
//                        TaskManager holder = new TaskManager(CreateTask.this);
//
//                        // To check if user wants to edit a task:
//                        // This is done because 'data' is only passed when a user wants to edit a task.
//                        if(getIntent().hasExtra("data")){
//                            Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
//                            HashMap<String,Object> taskJson = new Gson().fromJson(getIntent().getStringExtra("data"), mapType);
//                            taskJson.put("name", taskName.getText().trim());
//                            taskJson.put("time", calendar.getTimeInMillis());
//                            taskJson.put("category", taskCategory.getText().trim());
//                            taskJson.put("status", TaskStatus.Pending.name());
//
//                            TaskModel model = new TaskModel(taskJson);
//
//                            holder.updateTask(model, true);
//
//                            finish();
//
//                        }
//                        else{
//                            HashMap<String,Object> taskJson = new HashMap<>();
//                            //To set the is to "#TASK-(The index the model would have when it inserted into the list)
//                            taskJson.put("id", "#TASK-" + holder.getTasks().size());
//                            taskJson.put("globalId", taskJson.get("id"));
//                            taskJson.put("name", taskName.getText().trim());
//                            taskJson.put("time", calendar.getTimeInMillis());
//                            taskJson.put("category", taskCategory.getText().trim());
//                            taskJson.put("notifId", String.valueOf((int) AlarmManager.NOTIF_ID));
//                            AlarmManager.NOTIF_ID++;
//
//                            TaskModel model = new TaskModel(taskJson);
//                            holder.addTask(model, true);
//                            finish();
//                        }
//
//
//
//
//
//                    }
//                }, 2500);
//            }
//        });


    }

    void disableAll(){
//        taskName.setEnabled(false);
//        taskCategory.setEnabled(false);
//        taskDate.setEnabled(false);
//        taskTime.setEnabled(false);
    }
    void enableAll(){
//        taskName.setEnabled(true);
//        taskCategory.setEnabled(true);
//        taskDate.setEnabled(true);
//        taskTime.setEnabled(true);
    }
    void showDatePickerDialog(boolean start){
        final TextView target = start? startDateText: endDateText;
        final Calendar startDate = Calendar.getInstance();
        final Calendar endDate = Calendar.getInstance();
        final Calendar targetCalendar = start? startDate: endDate;


        final SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if(!target.getText().toString().contains("Today") && !target.getText().toString().contains("---")){
            try {
                targetCalendar.setTime(format.parse(target.getText().toString()));
                // Set the year to the current year. The SDF doesn't cover year.
                targetCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        final int year = targetCalendar.get(Calendar.YEAR);
        final int month = targetCalendar.get(Calendar.MONTH);
        final int day = targetCalendar.get(Calendar.DAY_OF_MONTH);

        final Calendar maxCal = Calendar.getInstance();
        maxCal.set(Calendar.MONTH, month ==12? 1: month+1);
        maxCal.set(Calendar.YEAR, month==12? year:year+1);


        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTask.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                final Calendar picked = Calendar.getInstance();
                final Calendar now = (Calendar) picked.clone();
                picked.set(Calendar.YEAR, _year);
                picked.set(Calendar.MONTH, _month);
                picked.set(Calendar.DAY_OF_MONTH, _day);

                final String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(picked.getTime());

                target.setText(picked.get(Calendar.DAY_OF_YEAR)==now.get(Calendar.DAY_OF_YEAR)?"Today":date);

                if(start){
                    endDateText.setText("---");
                    endTimeText.setText("---");
                }

            }
        }, year,month,day);
        if(!start){
            final Calendar _startDate = Calendar.getInstance();
            if(!startDateText.getText().toString().contains("Today") && !startDateText.getText().toString().contains("---")){
                try {
                    _startDate.setTime(format.parse(startDateText.getText().toString()));
                    // Set the year to the current year. The SDF doesn't cover year.
                    _startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            datePickerDialog.getDatePicker().setMinDate(_startDate.getTimeInMillis());
        }
        datePickerDialog.getDatePicker().setMaxDate(maxCal.getTimeInMillis());
        datePickerDialog.setOnCancelListener(dialogInterface -> {
            if(start){
                return;
            }
            target.setText("---");
        });

        datePickerDialog.show();
    }

    void showTimePickerDialog(boolean start){
        final Calendar _calendar = Calendar.getInstance();
        final int hour = _calendar.get(Calendar.HOUR);
        final int minute = _calendar.get(Calendar.MINUTE);


        final TextView target = start? startTimeText: endTimeText;


        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTask.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                final Calendar picked = Calendar.getInstance();
                picked.set(Calendar.HOUR_OF_DAY, hour);
                picked.set(Calendar.MINUTE, minute);

                final String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(picked.getTime()).toUpperCase();
                target.setText(time);

                if(startDateText.getText().toString().equalsIgnoreCase(endDateText.getText().toString())){
                    endTimeText.setText("---");
                }
            }

        },hour,minute, false);
        timePickerDialog.setOnCancelListener(dialogInterface -> {
            if(start){
                return;
            }
            target.setText("---");
        });
        timePickerDialog.create();
        timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.themeColor));
        timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));

        timePickerDialog.show();
    }

    void refreshCategories(){
        workLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.redTransparent)));
        personalLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blueTransparent)));
        uncategorizedLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellowTransparent)));
        studyLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenTransparent)));

        workText.setTextColor(getResources().getColor(R.color.red));
        personalText.setTextColor(getResources().getColor(R.color.blue));
        uncategorizedText.setTextColor(getResources().getColor(R.color.yellow));
        studyText.setTextColor(getResources().getColor(R.color.themeColor));

        if(category.contains("uncategorized")){
            uncategorizedLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            uncategorizedText.setTextColor(getResources().getColor(R.color.white));
        }
        if(category.contains("work")){
            workLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            workText.setTextColor(getResources().getColor(R.color.white));
        }
        if(category.contains("personal")){
            personalLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
            personalText.setTextColor(getResources().getColor(R.color.white));
        }
        if(category.contains("study")){
            studyLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
            studyText.setTextColor(getResources().getColor(R.color.white));
        }


    }

    private static long parseStringToTime(String timeString) {
        try {
            // Try parsing without handling scientific notation
            return Long.parseLong(timeString);
        } catch (NumberFormatException e) {
            // If parsing fails, try parsing with scientific notation
            try {
                DecimalFormat df = new DecimalFormat("#");
                Number number = df.parse(timeString);
                assert number != null;
                return number.longValue();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return 0; // or throw an exception as needed
            }
        }
    }



    void showMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}