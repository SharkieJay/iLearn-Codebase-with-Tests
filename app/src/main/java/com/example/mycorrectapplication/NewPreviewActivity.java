package com.example.mycorrectapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.DataService;
import com.example.mycorrectapplication.database.student.StudentHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewPreviewActivity extends AppCompatActivity {
    String question, ans1, ans2, ans3, ans4, type, correct;
    VideoView videoView;
    File imageFile, audioFile;
    Bitmap myBitmap;
    Handler handler;
    JSONObject json;
    Runnable runnable;
    DataService service;
    MediaPlayer mp;

    long currentTime, time;
    static int count;

    int totalCount = 0, attemptCount = 0, correctInt;

    CompetencyHelper calcComp = new CompetencyHelper();
    JSONObject obj = new JSONObject();
    private String studentId, lessonName, classroomId, learningStyle = "Read/Write";
    StudentHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Bundle bundle = getIntent().getExtras();
        String uri = bundle.getString("uri");
        lessonName = bundle.getString("lessonName");
        studentId = bundle.getString("studentId");
        classroomId = bundle.getString("classId");
        count = 0;
        service = new DataService(this);
        sh = new StudentHelper(this);
        json = service.readJSON(lessonName);
        mp = new MediaPlayer();

        try {
            JSONArray jsonArray = json.getJSONArray("questions");
            jsonArray = Util.sortJsonArray(jsonArray);
            json.put("questions", jsonArray);
            readJSON(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(json);
        videoView = findViewById(R.id.my_video);
        //videoView.setVideoPath("/storage/emulated/0/Movies/Video.mp4");
        videoView.setVideoPath(uri);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            videoView.pause();
            currentTime = videoView.getCurrentPosition();
            switch (type) {
                case "MCQ":
                    viewMcqQuestion(question, ans1, ans2, ans3, ans4);
                    break;
                case "SHORT":
                    viewShortAnsQuestion(question);
                    break;
                default:
                    break;
            }
            count++;
        };
        videoView.setOnCompletionListener(mp -> {
            this.mp.reset();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("lessonCompleted", true);
            resultIntent.putExtra("completedLessonName", lessonName);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        videoView.start();
        handler.postDelayed(runnable, time);
        learningStyle = "Kinesthetic";
        try {
            if (studentId != null) {
                learningStyle = sh.getLearningStyle(studentId);
                obj.put("difficulty_level", "L");
                obj.put("total_weight", 0);
                obj.put("final_score", 0F);
                obj.put("cumulative_total", 0);
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void viewMcqQuestion(String question, String ans1, String ans2, String ans3, String ans4){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.preview_mcq, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getX() < 0 || motionEvent.getX() > popupView.getWidth()) return true;
                if (motionEvent.getY() < 0 || motionEvent.getY() > popupView.getHeight()) return true;
                return false;
            }
        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView questionView = (TextView) popupView.findViewById(R.id.mcqQuestion);
        ImageView imageView = (ImageView) popupView.findViewById(R.id.imgBox);
        if ((imageFile != null && imageFile.exists()) && (learningStyle.equals("Visual") || learningStyle.equals("Kinesthetic"))){
            //Log.d("My Image Path1: ", String.valueOf(imageFile));
            myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }else{
            imageView.setVisibility(View.GONE);
        }

        playAudio();

        RadioGroup radioGroup = (RadioGroup) popupView.findViewById(R.id.mcq_ans_group);
        RadioButton radioButton_1 = (RadioButton) popupView.findViewById(R.id.btn_1);
        RadioButton radioButton_2 = (RadioButton) popupView.findViewById(R.id.btn_2);
        RadioButton radioButton_3 = (RadioButton) popupView.findViewById(R.id.btn_3);
        RadioButton radioButton_4 = (RadioButton) popupView.findViewById(R.id.btn_4);

        questionView.setText(question);
        radioButton_1.setText(ans1);
        radioButton_2.setText(ans2);
        radioButton_3.setText(ans3);
        radioButton_4.setText(ans4);

        questionView.setTextColor(Color.BLACK);
        radioButton_1.setTextColor(Color.BLACK);
        radioButton_2.setTextColor(Color.BLACK);
        radioButton_3.setTextColor(Color.BLACK);
        radioButton_4.setTextColor(Color.BLACK);

        questionView.setTextSize(20);
        radioButton_1.setTextSize(20);
        radioButton_2.setTextSize(20);
        radioButton_3.setTextSize(20);
        radioButton_4.setTextSize(20);

        String ans = "radioButton_"+correct;
        Button submit = (Button) popupView.findViewById(R.id.submit_mcq);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                boolean answeredCorrectly = false;
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
                } else {
                    if (radioButton_1.isChecked() && correctInt == 1) {
                        answeredCorrectly = true;
                    } else if (radioButton_2.isChecked() && correctInt == 2) {
                        answeredCorrectly = true;
                    } else if (radioButton_3.isChecked() && correctInt == 3) {
                        answeredCorrectly = true;
                    } else if (radioButton_4.isChecked() && correctInt == 4) {
                        answeredCorrectly = true;
                    } else {
                        attemptCount++;
                        if (attemptCount == 5) {
                            try {
                                if (studentId != null) {
                                    obj.put("attempt", attemptCount+1);
                                    obj = calcComp.calculateNextDifficultyLevel(obj, count);
                                }
                            } catch (JSONException e) { e.printStackTrace(); }
                            Toast.makeText(getApplicationContext(), "Maximum attempts exceeded", Toast.LENGTH_SHORT).show();
                            mp.reset();
                            videoView.start();
                            popupWindow.dismiss();
                            attemptCount = 0;
                            if (!(count >= totalCount)) {
                                try {
                                    readJSON(json);
                                } catch (JSONException e) { e.printStackTrace(); }
                                handler.postDelayed(runnable, time - currentTime);
                            } else {
                                try {
                                    if (studentId != null)
                                        sh.updateCompetency(studentId, classroomId, lessonName, obj.getString("difficulty_level"));
                                } catch (JSONException e) { e.printStackTrace(); }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if (answeredCorrectly) {
                    try {
                        if (studentId != null) {
                        obj.put("attempt", attemptCount+1);
                        obj = calcComp.calculateNextDifficultyLevel(obj, count);
                    }
                    } catch (JSONException e) { e.printStackTrace(); }
                    Toast.makeText(getApplicationContext(), "Correct Answer!", Toast.LENGTH_SHORT).show();
                    if (!(count >= totalCount)) {
                        try {
                            readJSON(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.postDelayed(runnable, time - currentTime);
                    } else {
                        try {
                            sh.updateCompetency(studentId, classroomId, lessonName, obj.getString("difficulty_level"));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                    mp.reset();
                    videoView.start();
                    popupWindow.dismiss();
                }
            }
        });
    }

    public void viewShortAnsQuestion(String question){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.preview_short, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getX() < 0 || motionEvent.getX() > popupView.getWidth()) return true;
                if (motionEvent.getY() < 0 || motionEvent.getY() > popupView.getHeight()) return true;
                return false;
            }
        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView questionView = (TextView) popupView.findViewById(R.id.shortQuestion);
        ImageView imageView = (ImageView) popupView.findViewById(R.id.imgBox);
        //Log.d("My Image Path1: ", String.valueOf(imageFile));
        if ((imageFile != null && imageFile.exists()) && (learningStyle.equals("Visual") || learningStyle.equals("Kinesthetic"))){
            myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        } else {
            imageView.setVisibility(View.GONE);
        }

        playAudio();

        EditText answerView = (EditText) popupView.findViewById(R.id.shortAnswer);

        questionView.setText(question);
        questionView.setTextSize(20);
        questionView.setTextColor(Color.BLACK);
        answerView.setTextSize(20);
        answerView.setTextColor(Color.BLACK);

        Button submit = (Button) popupView.findViewById(R.id.submit_short);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shortAns = answerView.getText().toString();
                if (shortAns.equalsIgnoreCase(correct)) {
                    try {
                        if (studentId != null) {
                            obj.put("attempt", attemptCount+1);
                            obj = calcComp.calculateNextDifficultyLevel(obj, count);
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                    Toast.makeText(getApplicationContext(), "Correct Answer!", Toast.LENGTH_SHORT).show();
                    attemptCount = 0;
                    if (!(count >= totalCount)) {
                        try {
                            readJSON(json);
                        } catch (JSONException e) { e.printStackTrace(); }
                        handler.postDelayed(runnable, time - currentTime);
                    } else {
                        try {
                            sh.updateCompetency(studentId, classroomId, lessonName, obj.getString("difficulty_level"));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                    mp.reset();
                    videoView.start();
                    popupWindow.dismiss();
                } else {
                    attemptCount++;
                    if (attemptCount == 5) {
                        try {
                            if (studentId != null) {
                                obj.put("attempt", attemptCount+1);
                                obj = calcComp.calculateNextDifficultyLevel(obj, count);
                            }
                        } catch (JSONException e) { e.printStackTrace(); }
                        Toast.makeText(getApplicationContext(), "Maximum attempts exceeded", Toast.LENGTH_SHORT).show();
                        mp.reset();
                        videoView.start();
                        popupWindow.dismiss();
                        attemptCount = 0;
                        if (!(count >= totalCount)) {
                            try {
                                readJSON(json);
                            } catch (JSONException e) { e.printStackTrace(); }
                            handler.postDelayed(runnable, time - currentTime);
                        } else {
                            try {
                                if (studentId != null)
                                    sh.updateCompetency(studentId, classroomId, lessonName, obj.getString("difficulty_level"));
                            } catch (JSONException e) { e.printStackTrace(); }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void playAudio() {
        if ((audioFile != null) && (learningStyle.equals("Aural") || learningStyle.equals("Kinesthetic"))) {
            audioFile.setReadable(true, false);
            try {
                final InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(audioFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mp.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build()
                );
            }
            try {
                mp.setDataSource(this, Uri.fromFile(audioFile));
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });
                mp.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Only reads json file
    public void readJSON(JSONObject json) throws JSONException {
        JSONArray arr = (JSONArray) json.get("questions");
        totalCount = arr.length();

        String difficulty_level = !obj.has("difficulty_level") ? "l" : obj.getString("difficulty_level");

        try {
            time = Long.parseLong((String) ((JSONObject) arr.get(count)).get("Timestamp"));
        } catch (JSONException e) { e.printStackTrace(); }

        try {
            type = (String) ((JSONObject) arr.get(count)).get("Type");
        } catch (JSONException e) { e.printStackTrace(); }

        switch (type) {
            case "MCQ":
                try {
                    if (difficulty_level.equalsIgnoreCase("l")) {
                        question = (String) ((JSONObject) arr.get(count)).get("Question");
                        ans1 = (String) ((JSONObject) arr.get(count)).get("Answer1");
                        ans2 = (String) ((JSONObject) arr.get(count)).get("Answer2");
                        ans3 = (String) ((JSONObject) arr.get(count)).get("Answer3");
                        ans4 = (String) ((JSONObject) arr.get(count)).get("Answer4");
                        correctInt = Integer.parseInt((String) ((JSONObject) arr.get(count)).get("Answer"));
                        imageFile = ((JSONObject) arr.get(count)).has("image") ? new File((String) ((JSONObject) arr.get(count)).get("image")) : null;
                        audioFile = ((JSONObject) arr.get(count)).has("audio") ? new File((String) ((JSONObject) arr.get(count)).get("audio")) : null;
                    } else if (difficulty_level.equalsIgnoreCase("m")) {
                        question = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Question");
                        ans1 = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Answer1");
                        ans2 = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Answer2");
                        ans3 = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Answer3");
                        ans4 = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Answer4");
                        correctInt = Integer.parseInt(((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Answer"));
                        imageFile = ((JSONObject) arr.get(count)).has("image") ? new File((String) ((JSONObject) arr.get(count)).get("image")) : null;
                        audioFile = ((JSONObject) arr.get(count)).has("audio") ? new File((String) ((JSONObject) arr.get(count)).get("audio")) : null;
                    } else if (difficulty_level.equalsIgnoreCase("h")) {
                        question = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Question");
                        ans1 = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Answer1");
                        ans2 = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Answer2");
                        ans3 = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Answer3");
                        ans4 = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Answer4");
                        correctInt = Integer.parseInt(((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Answer"));
                        imageFile = ((JSONObject) arr.get(count)).has("image") ? new File((String) ((JSONObject) arr.get(count)).get("image")) : null;
                        audioFile = ((JSONObject) arr.get(count)).has("audio") ? new File((String) ((JSONObject) arr.get(count)).get("audio")) : null;
                    }

                } catch (JSONException e) { e.printStackTrace(); }
                break;
            case "SHORT":
                try {
                    if (difficulty_level.equalsIgnoreCase("l")) {
                        question = (String) ((JSONObject) arr.get(count)).get("Question");
                        correct = (String) ((JSONObject) arr.get(count)).get("Answer");
                        imageFile = ((JSONObject) arr.get(count)).has("image") ? new File ((String) ((JSONObject) arr.get(count)).get("image")): null;
                        audioFile = ((JSONObject) arr.get(count)).has("audio") ? new File ((String) ((JSONObject) arr.get(count)).get("audio")): null;
                    } else if (difficulty_level.equalsIgnoreCase("m")) {
                        question = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Question");
                        correct = ((JSONObject) ((JSONObject) arr.get(count)).get("medium")).getString("Answer");
                        imageFile = ((JSONObject) arr.get(count)).has("image") ? new File ((String) ((JSONObject) arr.get(count)).get("image")): null;
                        audioFile = ((JSONObject) arr.get(count)).has("audio") ? new File ((String) ((JSONObject) arr.get(count)).get("audio")): null;
                    } else if (difficulty_level.equalsIgnoreCase("h")) {
                        question = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Question");
                        correct = ((JSONObject) ((JSONObject) arr.get(count)).get("high")).getString("Answer");
                        imageFile = ((JSONObject) arr.get(count)).has("image") ? new File ((String) ((JSONObject) arr.get(count)).get("image")): null;
                        audioFile = ((JSONObject) arr.get(count)).has("audio") ? new File ((String) ((JSONObject) arr.get(count)).get("audio")): null;
                    }
                } catch (JSONException e) { e.printStackTrace(); }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mp.reset();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("lessonCompleted", false);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}