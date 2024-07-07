package com.theteam.taskz;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
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

    private TextInputFormField taskName,taskDate,taskTime,taskCategory;
    private LoadableButton button;
    private TextView title_text,subtitle_text;
    private Calendar calendar = Calendar.getInstance();


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
    void showDatePickerDialog(){
        final Calendar _calendar = Calendar.getInstance();
        final int year = _calendar.get(Calendar.YEAR);
        final int month = _calendar.get(Calendar.MONTH);
        final int day = _calendar.get(Calendar.DAY_OF_MONTH);

        final Calendar maxCal = Calendar.getInstance();
        maxCal.set(Calendar.MONTH, month ==12? 1: month+1);
        maxCal.set(Calendar.YEAR, month==12? year:year+1);


        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTask.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                calendar.set(Calendar.YEAR, _year);
                calendar.set(Calendar.MONTH, _month);
                calendar.set(Calendar.DAY_OF_MONTH, _day);

//                showMessage("date picked");
//                taskDate.setText(new SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(calendar.getTime()));
//                taskTime.setText("");

            }
        }, year,month,day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCal.getTimeInMillis());

        datePickerDialog.show();
    }

    void showTimePickerDialog(){
        final Calendar _calendar = Calendar.getInstance();
        final int hour = _calendar.get(Calendar.HOUR);
        final int minute = _calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTask.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//                calendar.set(Calendar.HOUR_OF_DAY, hour);
//                calendar.set(Calendar.MINUTE, minute);
//                taskTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.getTime()));
            }
        },hour,minute, false);
        timePickerDialog.show();
    }

    void showCategoryDialog(){
        BottomSheetDialog dialog = new BottomSheetDialog(CreateTask.this);
        View v = getLayoutInflater().inflate(R.layout.select_category, null);
        final LinearLayout work = v.findViewById(R.id.work);
        final LinearLayout study = v.findViewById(R.id.study);
        final LinearLayout personal = v.findViewById(R.id.personal);
        final LinearLayout uncategorized = v.findViewById(R.id.uncategorized);

        work.setOnClickListener(view -> {
            taskCategory.setText("Work");
            dialog.dismiss();
        });
        study.setOnClickListener(view -> {
            taskCategory.setText("Study");
            dialog.dismiss();

        });
        personal.setOnClickListener(view -> {
            taskCategory.setText("Personal");
            dialog.dismiss();

        });
        uncategorized.setOnClickListener(view -> {
            taskCategory.setText("Uncategorized");
            dialog.dismiss();
        });
        dialog.setContentView(v);
        dialog.setDismissWithAnimation(true);
        dialog.show();

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