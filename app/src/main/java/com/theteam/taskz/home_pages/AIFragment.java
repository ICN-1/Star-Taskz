package com.theteam.taskz.home_pages;

import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.LoginActivity;
import com.theteam.taskz.utilities.AlarmManager;
import com.theteam.taskz.utilities.DateComparator;
import com.theteam.taskz.R;
import com.theteam.taskz.utilities.EmojiRemover;
import com.theteam.taskz.utilities.ReadAssetsFile;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.utilities.ThemeManager;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.view_models.TypeWriterTextView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;

public class AIFragment extends Fragment {

    private TextToSpeech speech;
    private LinearLayout record_button;
    private ImageView record_icon;
    private LottieAnimationView ai_lottie;

    private TypeWriterTextView ai_text;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private GenerativeModel star_taskz;
    private GenerativeModelFutures aiModel;

    private ListenableFuture<GenerateContentResponse> aiCurrentResponse;
    private ChatFutures chat;

    private boolean isListening = false;
    private boolean isThinking = false;

    private MediaPlayer recordMp, stopMp;

    private
    ArrayList<HashMap<String, String>> speeches = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ai_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        record_button = view.findViewById(R.id.record_button);
        record_icon = view.findViewById(R.id.record_icon);
        ai_lottie = view.findViewById(R.id.ai_lottie);
        ai_text = view.findViewById(R.id.ai_text);
        ai_text.setStartDelay(20);
        ai_text.setTypingInterval(50);
        ai_text.animateText("Hi there! Star here😊");
        recordMp = MediaPlayer.create(requireActivity().getApplicationContext(), R.raw.hangup);


        // Initialize speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        List<SafetySetting> safetySettings = new ArrayList<>();
        safetySettings.add(new SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH));
        safetySettings.add(new SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE));

        GenerationConfig.Builder generationConfigBuilder = new GenerationConfig.Builder();
        generationConfigBuilder.temperature = 1f;
        generationConfigBuilder.topK = 64;
        generationConfigBuilder.topP = 0.95f;

        GenerationConfig generationConfig = generationConfigBuilder.build();
        RequestOptions requestOptions = new RequestOptions();

        final Calendar today = Calendar.getInstance();

        String instructions;

        try {
            instructions = ReadAssetsFile.readAssetsFile(requireActivity().getApplicationContext().getAssets(), "Star_AI_Instructions.txt");
            Log.v("Asset Reader", "Opened AI Instructions from assets");
        } catch (IOException e) {
            instructions = requireActivity().getString(R.string.STAR_AI_INSTRUCTIONS);
            e.printStackTrace();
        }
        final UserModel user = new UserModel(requireActivity());

        star_taskz = new GenerativeModel(
                "gemini-1.5-pro-latest",
                getResources().getString(R.string.Google_AI_KEY),
                generationConfig,
                safetySettings,
                requestOptions,
                null,
                null,
                new Content.Builder()
                        .addText(instructions)
                        .addText("Today is " + new SimpleDateFormat("EEEE, MMM dd, yyyy.", Locale.getDefault()).format(today.getTime()) + " And the current time is " + new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(today.getTime()))
                        .addText("You now know the time !")
                        .addText("The first name of the client that you are attending to is " + user.firstName() + " and his/her last name is " + user.lastName())
                        .build()
        );
        aiModel = GenerativeModelFutures.from(star_taskz);


        List<Content> chatHistory = new ArrayList<>();
        chat = aiModel.startChat(chatHistory);


        // Set recognition listener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                isListening = false;
                record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                record_icon.setImageResource(R.drawable.record);
                // TO display and say that AI is thinking.
                isThinking = true;
                final boolean yes = new Random().nextBoolean();
                speak(yes?"Hmmm, Let me see...":"Hold on, give me a minute");
                ai_text.animateText(yes?"Let me see...":"Give me a minute");
                ai_lottie.setAnimation(new ThemeManager(requireActivity()).isDarkMode()? R.raw.ai_thinking_dark: R.raw.ai_thinking);
                ai_lottie.setScaleX(0.7f);
                ai_lottie.setScaleY(0.7f);
                ai_lottie.playAnimation();

            }

            @Override
            public void onError(int i) {
                Log.v("AI", String.valueOf(i));
                if(i == SpeechRecognizer.ERROR_AUDIO){
                    ai_text.animateText("Could not hear you :(");
                    final boolean yes = new Random().nextBoolean();
                    speechStart(yes? "Whoa Whoa.\n Slow down. Chill and Talk Calm" : "Oops, Sorry pal, Not able to hear you.");
                }
                if(i == SpeechRecognizer.ERROR_NO_MATCH){
                    ai_text.animateText("Try to be more clear :(");
                    final boolean yes = new Random().nextBoolean();
                    speechStart(yes? "Whoa Whoa.\n Slow down. Chill and Talk Calm" : "Oops, Sorry pal, Not able to hear you.");
                }
                if(i== SpeechRecognizer.ERROR_CLIENT){
                    ai_text.animateText("Something went wrong :(");
                }
                if(i == SpeechRecognizer.ERROR_NETWORK){
                    ai_text.animateText("Timeout ! :(");
                }
                isListening = false;
                record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                record_icon.setImageResource(R.drawable.record);
            }

            @Override
            public void onResults(Bundle bundle) {
                isListening = false;
                record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                record_icon.setImageResource(R.drawable.record);
                if(!isListening){
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (results != null && !results.isEmpty()) {
                        String recognizedText = results.get(0);
                        askAi(recognizedText);

                    }
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
        
        // Initialize speech
        speech = new TextToSpeech(requireActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
//                    speech.setLanguage(new Locale("en", "US"));
//                    speech.setPitch(0.3f);
                    speech.setSpeechRate(1.5f);
                    AlarmManager.speech = speech;




                    speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onRangeStart(String utteranceId, int start, int end, int frame) {
                            super.onRangeStart(utteranceId, start, end, frame);
                            // We don't want to change the text to what ai says if it is thinking.
                            if(isThinking){
                                return;
                            }
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final String text = speeches.get(Integer.parseInt(utteranceId)-1).get("speech").substring(0,end+1);
                                    ai_text.setText(text);


                                }
                            });
                        }

                        @Override
                        public void onDone(String s) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(s.trim().toLowerCase(Locale.getDefault()).equals(speeches.get(speeches.size()-1).get("utteranceId"))){
                                        speechStop();
                                    }
                                }
                            });


                        }

                        @Override
                        public void onError(String s) {

                        }
                    });

                    record_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isListening){
                                stopListening();
                                return;
                            }
                            startListening();
                        }
                    });


                }
            }
        });


        //This is section is made to automatically talk about Star Taskz when a user is new.
        if(requireActivity().getIntent().hasExtra("first")){
            requireActivity().getIntent().removeExtra("first");
            askAi("Hey There! I just heard about Star Tasks and " +
                    "I will like you to explain a lot about Star Tasks, " +
                    "It's creators, All It's features and how to go about it. " +
                    "And also please tell me more about you");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(speechRecognizer != null){
            speechRecognizer.destroy();
        }
    }

    private void askAi(String question){
        Content content = new Content.Builder()
                .addText(question)
                .build();


        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> aiResponse = chat.sendMessage(content);
        aiCurrentResponse = aiResponse;
        Futures.addCallback(aiResponse, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse generateContentResponse) {
                String response = generateContentResponse.getText();
                if(getActivity() != null){
                    requireActivity().runOnUiThread(() -> {
                        decodeAIResponse(response);
                    });
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                final String message = throwable.getMessage();
                if(message.contains("safety")){
                    speechStart("I've been instructed by my creators not to attest to explicit, dangerous or private content.");
                }
                else if(throwable.getMessage().equalsIgnoreCase("Resource has been exhausted (e.g. check quota).")){
                    speechStart("Oops, an unprecedented error occurred. Please be more clear");
                }
                else if(!throwable.getMessage().toLowerCase().contains("chat instance")){
                    speechStart(throwable.getMessage());
                }
                Log.v("AI", throwable.getMessage());
            }
        }, executor);
    }
    private void decodeAIResponse(String aiString){
        String string = aiString.trim();
        Log.v("AI", string);

        //That is if the ai didn't understand the question or fully utilize the given parameters.
//        if(string.startsWith("```json") || string.startsWith("```")){
//            speechStart("Please specify the task name and time properly");
//            return;
//        }
        if(string.contains("{") && string.contains("}")){
           string = string.replaceAll("\\\\\\\\", "");
        }
        int startIndex = string.indexOf("{");
        int endIndex = string.lastIndexOf("}");
        if(string.contains("{") && string.contains("}") && startIndex != -1 && endIndex != -1 && startIndex < endIndex){
            string=string.substring(startIndex,endIndex+1);
        }
        if(isJSON(string)){
            Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
            HashMap<String,Object> rawJson = new Gson().fromJson(string, mapType);
            HashMap<String,Object> taskJson = new HashMap<>();
            rawJson.forEach((key,value)->{
                taskJson.put(key.toLowerCase(), value);
            });
            if(taskJson.get("request-type").equals("create-task")){
                createTask(taskJson);
                return;
            }
            if(taskJson.get("request-type").equals("list-tasks")){
                listTasks(taskJson);
                return;
            }
            if(taskJson.get("request-type").equals("logout")){
                new TaskManager(requireActivity()).clearTasks();
                UserModel.clearUserData(requireActivity());

                Intent i = new Intent(requireActivity().getApplicationContext(), LoginActivity.class);
                requireActivity().startActivity(i);
                requireActivity().finish();
            }

        }
        else{
            if(string.toLowerCase().contains("content generation stopped")){
                if(string.toLowerCase().contains("safety")){
                    speechStart("Sorry Pal, I've been instructed not to mention anything Safety ");
                }

            }
            else{
                speechStart(EmojiRemover.removeEmojis(string));
            }
        }
    }
    private void createTask(HashMap<String,Object> taskJson){
        if(!taskJson.containsKey("time") || taskJson.get("time") == null){
            speechStart("What time should i set your task?");
            return;
        }
        TaskManager holder = new TaskManager(requireActivity());

        HashMap<String,Object> _taskJson = new HashMap<>();
        //To set the is to "#TASK-(The index the model would have when it inserted into the list)

        Calendar time = Calendar.getInstance();
        //If at all the AI misbehaves and passes the date as null, we set it to today on our end also
        Calendar g = taskJson.get("date") == null? Calendar.getInstance(): getCalendarFromDate(taskJson.get("date").toString());
        time.setTimeInMillis(g.getTimeInMillis());
        Calendar h = getCalendarFromTime(taskJson.get("time").toString());
        time.set(Calendar.HOUR_OF_DAY, h.get(Calendar.HOUR_OF_DAY));
        time.set(Calendar.MINUTE, h.get(Calendar.MINUTE));
        final String now = new SimpleDateFormat("EEEE, MMM dd, HH:mm a", Locale.getDefault()).format(time.getTime());
        Log.d("AI", now);

        Calendar calendar = Calendar.getInstance();
        if(calendar.getTime().after(time.getTime())){
            speechStart("Creating a task at an expired date or time is not possible");
            return;
        }
        final String fr = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(taskJson.get("date") == null ? Calendar.getInstance().getTime(): getCalendarFromDate(taskJson.get("date").toString()).getTime());
        speechStart("Creating task : "+ "'" +  taskJson.get("name") + "' at " + taskJson.get("time") + " on " + fr);

//            //If the time given is not older or past.
//            //This is checked due to the AIs current set time
//            //If the set time is greater than the current time.
//            if(g.get(Calendar.YEAR)>=time.get(Calendar.YEAR)){
//                time.set(Calendar.YEAR, g.get(Calendar.YEAR));
//                //If the set month is greater than or equals to the current month.
//                if(g.get(Calendar.MONTH)>=time.get(Calendar.MONTH)){
//                    time.set(Calendar.MONTH, g.get(Calendar.MONTH));
//                    //If the set day is greater than or equals to the current day.
//                    if(g.get(Calendar.DAY_OF_MONTH)>=time.get(Calendar.DAY_OF_MONTH)){
//                        time.set(Calendar.DAY_OF_MONTH, g.get(Calendar.DAY_OF_MONTH));
//                    }
//                }
//            }


//            //If the time given is not older or past.
//            //This is checked due to the AIs current set time
//            if(g.get(Calendar.DAY_OF_YEAR)>=time.get(Calendar.DAY_OF_YEAR)){
//                //If the day is the same
//                if(g.get(Calendar.DAY_OF_YEAR)==time.get(Calendar.DAY_OF_YEAR)){
//                    //If the hour is equals to or after the current hour
//                    if(g.get(Calendar.HOUR_OF_DAY) >= time.get(Calendar.HOUR_OF_DAY)){
//                        time.set(Calendar.HOUR_OF_DAY, g.get(Calendar.HOUR_OF_DAY));
//                        // if the hour is equal to the current hour
//                        if(g.get(Calendar.HOUR_OF_DAY) == time.get(Calendar.HOUR_OF_DAY)){
//                            // if the minute is greater than or equals to the current minute
//                            if(g.get(Calendar.MINUTE) >= time.get(Calendar.MINUTE)){
//                                time.set(Calendar.MINUTE, g.get(Calendar.MINUTE));
//                            }
//                        }else{
//                            // We can set the minute since they are in the same day but the hour is after the current hour
//                            time.set(Calendar.MINUTE, g.get(Calendar.MINUTE));
//                        }
//                    }
//                    //We do nothing since the its the same day but the hour set has been past.
//                }
//                //else set the hour and the minute since the days are not the same but the tasks day is higher than the current day.
//                else{
//                    time.set(Calendar.HOUR_OF_DAY, g.get(Calendar.HOUR_OF_DAY));
//                    time.set(Calendar.MINUTE, g.get(Calendar.MINUTE));
//                }
//            }



        _taskJson.put("id", "#TASK-" + holder.getTasks().size());
        _taskJson.put("globalId", "#TASK-" + holder.getTasks().size());
        _taskJson.put("name", taskJson.get("name"));
        _taskJson.put("time", time.getTimeInMillis());
        _taskJson.put("category", taskJson.get("category"));
        _taskJson.put("notifId", String.valueOf((int) AlarmManager.NOTIF_ID));

        TaskModel model = new TaskModel(_taskJson);
        holder.addTask(model, false);
    }
    private void listTasks(HashMap<String,Object> criteria){
        TaskManager manager = new TaskManager(requireActivity());
        List<TaskModel> tasks = manager.getTasks();
        List<TaskModel> filteredTasks = new ArrayList<>();
        // First we filter based on date.
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        for(final TaskModel task: tasks){
            final Calendar dateCalendar = getCalendarFromDate(criteria.get("date").toString());
            Log.v("AI", dateFormat.format(task.date.getTime()));

            if(dateFormat.format(task.date.getTime()).equals(criteria.get("date"))){

                //if the time is not null we filter by time as well
                if(criteria.get("time") != null){
                    if(timeFormat.format(task.date.getTime()).equalsIgnoreCase(criteria.get("time").toString())){
                        //If status is null i.e (not specified meaning all) we add the task by default
                        if(criteria.get("status")==null){
                            //If category is null i.e (not specified meaning all) we add the task by default
                            if(criteria.get("category")==null){
                                filteredTasks.add(task);
                            }
                            // else we only add it if the category is the same with the specified category
                            else{
                                if(criteria.get("category").toString().equalsIgnoreCase(task.category)){
                                    filteredTasks.add(task);
                                }
                            }
                        }
                        // else we only add it if the category is the same with the specified category
                        else{
                            if(criteria.get("status").toString().equalsIgnoreCase(task.status.name())){
                                //If category is null i.e (not specified meaning all) we add the task by default
                                if(criteria.get("category")==null){
                                    filteredTasks.add(task);
                                }
                                // else we only add it if the category is the same with the specified category
                                else{
                                    if(criteria.get("category").toString().equalsIgnoreCase(task.category)){
                                        filteredTasks.add(task);
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    //If status is null i.e (not specified meaning all) we add the task by default
                    if(criteria.get("status")==null){
                        //If category is null i.e (not specified meaning all) we add the task by default
                        if(criteria.get("category")==null){
                            filteredTasks.add(task);
                        }
                        // else we only add it if the category is the same with the specified category
                        else{
                            if(criteria.get("category").toString().equalsIgnoreCase(task.category)){
                                filteredTasks.add(task);
                            }
                        }
                    }
                    // else we only add it if the category is the same with the specified category
                    else{
                        if(criteria.get("status").toString().equalsIgnoreCase(task.status.name())){
                            //If category is null i.e (not specified meaning all) we add the task by default
                            if(criteria.get("category")==null){
                                filteredTasks.add(task);
                            }
                            // else we only add it if the category is the same with the specified category
                            else{
                                if(criteria.get("category").toString().equalsIgnoreCase(task.category)){
                                    filteredTasks.add(task);
                                }
                            }
                        }
                    }
                }

            }

        }
        filteredTasks.sort(new DateComparator());

        if(tasks.isEmpty()){
            speechStart("You do not have any tasks");
        }
        else if(filteredTasks.isEmpty()){
            speechStart("No tasks concerning your descriptions were found");
        }
        else{
            StringBuilder str = new StringBuilder("I was able to find " + filteredTasks.size() + " task" + (filteredTasks.size() == 1 ? "" : "s") + " matching your description." + (filteredTasks.size() == 1 ? " Which is:" : " Which are:") + "\n");
            for (int i=0; i<filteredTasks.size(); i++){
                final TaskModel task = filteredTasks.get(i);
                boolean hasExpired = Calendar.getInstance().getTime().after(task.date.getTime());
                String s = (filteredTasks.size() == 1 ? "" : String.valueOf(i+1) + ") ")  + filteredTasks.get(i).name + " which start" + (hasExpired? "ed":"s") + " by ";
                s = s + timeFormat.format(task.date.getTime());
                // If it is not today i want to specify the day with the text as well
                if(!dateFormat.format(task.date.getTime()).equals(criteria.get("date"))){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
                    s = s + " " + simpleDateFormat.format(task.date.getTime());
                }
                s = s + ".\n";
                str.append(s);
            }
            speechStart(str.toString());

        }

    }
    private Calendar getCalendarFromTime(String time){
        //We are using 'hh' for the hour since the AI by default uses 12 hour format
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(Objects.requireNonNull(sdf.parse(time)));
            calendar.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        } catch (Exception e) {
            Log.e("AI", Objects.requireNonNull(e.getMessage()));
            showMessage("Something went wrong");
        }
        return calendar;

    }
    private Calendar getCalendarFromDate(String time){
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(Objects.requireNonNull(sdf.parse(time)));
            calendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        } catch (Exception e) {
            Log.e("AI", Objects.requireNonNull(e.getMessage()));
            showMessage("Something went wrong");
        }
        return calendar;

    }

    private void startListening(){
        speechStop();
        //To stop the ai from generating a response in case ai was interrupted by the user
        if(aiCurrentResponse != null){
            if(!aiCurrentResponse.isCancelled()){
                aiCurrentResponse.cancel(true);
            }
        }

        isListening = true;
        speechRecognizer.startListening(speechRecognizerIntent);
        isListening = true;
        ai_text.animateText("Listening...");
        record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        record_icon.setImageResource(R.drawable.stop);



    }
    private void stopListening(){
        isListening = false;
        speechRecognizer.stopListening();
//        ai_text.animateText("Hold on, give me a minute");

    }
    private void speechStart(String speech){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isThinking = false;
                if(new ThemeManager(requireActivity()).isDarkMode()){
                    ai_lottie.setAnimation(R.raw.ai_speaking_dark);
                }else{
                    ai_lottie.setAnimation(R.raw.ai_speaking_light);
                }
                ai_lottie.setScaleX(1.4f);
                ai_lottie.setScaleY(1.4f);
                speak(speech);
                ai_lottie.playAnimation();
                ai_text.setVisibility(View.GONE);
            }
        });

    }
    private void speechStop(){
        // We don't want to change the animation if it is thinking.
        if(isThinking){
            return;
        }
        speech.stop();
        ai_text.setVisibility(View.VISIBLE);
        ai_text.animateText("Need any assistance?");
        ai_lottie.setScaleX(1f);
        ai_lottie.setScaleY(1f);
        ai_lottie.setAnimation(R.raw.ai_not_speaking);
        ai_lottie.playAnimation();

    }
    private void speak(String textSpeech){
        if(speech.isSpeaking()){
            speech.stop();
        }
        speeches.clear();
        final String text = textSpeech.replaceAll("\\*", "");
        final String[] splitTexts =  text.split("(?<!\\bMr)(?<!\\bMrs)\\. ");


        for (int i = 0; i < splitTexts.length; i++) {
            StringBuilder resultBuilder = new StringBuilder();
            String part = splitTexts[i].trim();
            resultBuilder.append(part);
//            if (i < splitTexts.length - 1) {
//                if(!splitTexts[i + 1].isEmpty()){
//                    resultBuilder.append(splitTexts[i + 1].charAt(0)); // Append the first character of the next part
//                }
//            }
            HashMap<String, String> map = new HashMap<>();
            Log.e("AI", resultBuilder.toString());
            //The extra space was added because the speeches gotten during the onUtteranceProgressListener
            //always ends short 1. I.e it doesn't show the last text.
            map.put("speech", resultBuilder + " ");
            map.put("utteranceId", String.valueOf(speeches.size()+1));
            speeches.add(map);
        }

        for(int i=0;i<speeches.size();i++) {

            speech.speak(speeches.get(i).get("speech") + " ", i == 0 ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, speeches.get(i).get("utteranceId"));
        }
    }

    private boolean isJSON(String jsonString) {
        try {
            Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
            new Gson().fromJson(jsonString, mapType);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    void showMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
