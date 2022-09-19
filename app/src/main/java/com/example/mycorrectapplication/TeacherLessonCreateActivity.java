package com.example.mycorrectapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.DataService;
import com.example.mycorrectapplication.database.DatabaseHelper;
import com.example.mycorrectapplication.database.student.StudentHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class TeacherLessonCreateActivity extends AppCompatActivity {
    private static final int IMAGE_PICKER_SELECT = 1;
    long pausedPosition;
    VideoView videoView;
    Button btn_add_activity, btn_open, btn_help, btn_save, finishButton, btn_preview;
    File imageFile, audioFile;
    CheckBox imageAdded, questionAdded, soundclipAdded;
    int choice, questionCount = 0;
    JSONObject metadata;
    JSONArray questionsArray;

    String uri, classID, teacherID;
    EditText lessonName;
    boolean newLesson = true;

    DatabaseHelper dbHelper;
    SQLiteDatabase readableDB, writableDB;
    DataService service;

    private final String SHARED_PREF = "sharedPrefs", TEACHER_ID = "teacherId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_lesson_create);

        Bundle bundle = getIntent().getExtras();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        teacherID = sharedPreferences.getString(TEACHER_ID, "");
        classID = bundle.getString("classID", "");
        JSONObject updateLesson = null;
        try {
            if (bundle.getString("json") != null) updateLesson = new JSONObject(bundle.getString("json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lessonName = (EditText) findViewById(R.id.lesson_name);
        videoView = findViewById(R.id.my_video);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_add_activity = (Button) findViewById(R.id.btn_add_activity);
        btn_help = (Button) findViewById(R.id.btn_help);
        btn_preview = (Button) findViewById(R.id.btn_preview);

        btn_add_activity.setEnabled(false);
        btn_save.setVisibility(View.GONE);

        metadata = new JSONObject();
        dbHelper = new DatabaseHelper(this);
        service = new DataService(this);
        questionsArray = new JSONArray();
        readableDB = dbHelper.getReadableDatabase();
        writableDB = dbHelper.getWritableDatabase();

        if (updateLesson != null) editLesson(updateLesson);
        initObjects();
    }

    private void editLesson(JSONObject updateLesson){
        try {
            lessonName.setText(updateLesson.getString("name"));
            metadata = new JSONObject(updateLesson.getString("metadata"));
            questionsArray = metadata.getJSONArray("questions");
            questionsArray = Util.sortJsonArray(questionsArray);
            uri = updateLesson.getString("local_link");
            btn_open.setVisibility(View.GONE);
            lessonName.setFocusable(false);
            btn_add_activity.setEnabled(true);
            videoView.setVideoPath(uri);
            questionCount = questionsArray.length();
            newLesson = false;

            String name;
            LinearLayout ll;
            JSONObject question;

            for (int j = 0; j < questionsArray.length(); j++) {
                question = questionsArray.getJSONObject(j);
                ll = (LinearLayout) findViewById(R.id.activities_tab);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 0, 5, 0);

                Button btn_activity1 = new Button(TeacherLessonCreateActivity.this);
                btn_activity1.setText(getTimeString(Long.parseLong(question.getString("Timestamp"))));
                btn_activity1.setBackgroundColor(getResources().getColor(R.color.purple_200));
                btn_activity1.setLayoutParams(params);
                ll.addView(btn_activity1);

                if (question.getString("Type").equals("MCQ"))commonMCQActivityButton(btn_activity1);
                else commonShortActivityButton(btn_activity1);
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void initObjects() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.requestFocus();

        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaController.show(0);
            }
        });

        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(TeacherLessonCreateActivity.this, NewPreviewActivity.class);
                intentPreview.putExtra("uri", uri);
                intentPreview.putExtra("lessonName", lessonName.getText().toString());
                startActivity(intentPreview);
            }
        });

        btn_add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausedPosition = videoView.getCurrentPosition();
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_add, null);

                final PopupWindow popupWindow1 = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT, true);

                popupWindow1.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                dimBehind(popupWindow1);

                Button buttonNext = (Button) popupView.findViewById(R.id.next_step);
                RadioGroup questionType = (RadioGroup) popupView.findViewById(R.id.question_type_group);
                RadioButton btn_mcq = (RadioButton) questionType.findViewById(R.id.radio_mcq);
                RadioButton btn_short = (RadioButton) questionType.findViewById(R.id.radio_short);

                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popup_add_next_short, null);
                        final PopupWindow popupWindow2 =
                                new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                        popupWindow2.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        dimBehind(popupWindow2);

                        questionAdded = (CheckBox) popupView.findViewById(R.id.checkbox_question_short);
                        imageAdded = (CheckBox) popupView.findViewById(R.id.checkbox_image_short);
                        soundclipAdded = (CheckBox) popupView.findViewById(R.id.checkbox_soundclip_short);
                        Button questionButton = (Button) popupView.findViewById(R.id.add_short_question);
                        Button imageButton = (Button) popupView.findViewById(R.id.add_short_image);
                        Button audioButton = (Button) popupView.findViewById(R.id.add_short_soundclip);

                        finishButton = (Button) popupView.findViewById(R.id.finish_short);

                        imageAdded.setEnabled(false);
                        questionAdded.setEnabled(false);
                        soundclipAdded.setEnabled(false);
                        finishButton.setEnabled(false);

                        if (btn_mcq.isChecked()) {
                            choice = 1;
                            commonMCQActivityButton(questionButton);
                        } else if (btn_short.isChecked()) {
                            choice = 2;
                            commonShortActivityButton(questionButton);
                        }

                        audioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                pickIntent.setType("audio/*");
                                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(pickIntent,1);
//                                MediaPlayer mp = MediaPlayer.create(TeacherLessonCreateActivity.this, R.raw.slayer);
//                                mp.start();
                            }
                        });

                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickIntent.setType("image/*");
                                startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
                            }
                        });

                        finishButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (questionsArray.length() == 0) {
                                        ((JSONObject) questionsArray.get(0))
                                                .put("image", imageFile);
                                        ((JSONObject) questionsArray.get(0))
                                                .put("audio", audioFile);
                                    } else {
                                        ((JSONObject) questionsArray.get(questionsArray.length()-1))
                                                .put("image", imageFile);
                                        ((JSONObject) questionsArray.get(questionsArray.length()-1))
                                                .put("audio", audioFile);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    questionsArray = Util.sortJsonArray(questionsArray);
                                } catch (JSONException e) { e.printStackTrace(); }

                                if (metadata != null) {
                                    LinearLayout ll = (LinearLayout) findViewById(R.id.activities_tab);
                                    ll.removeAllViews();
                                    for (int j = 0; j < questionsArray.length(); j++) {
                                        JSONObject question = null;
                                        try {
                                            question = questionsArray.getJSONObject(j);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params.setMargins(5, 0, 5, 0);

                                            Button btn_activity1 = new Button(TeacherLessonCreateActivity.this);
                                            btn_activity1.setText(getTimeString(Long.parseLong(question.getString("Timestamp"))));
                                            btn_activity1.setBackgroundColor(getResources().getColor(R.color.purple_200));
                                            btn_activity1.setLayoutParams(params);
                                            ll.addView(btn_activity1);

                                            if (question.getString("Type").equals("MCQ"))commonMCQActivityButton(btn_activity1);
                                            else commonShortActivityButton(btn_activity1);
                                        } catch (JSONException e) { e.printStackTrace();}
                                    }
                                    btn_preview.setEnabled(false);
                                    btn_save.setVisibility(View.VISIBLE);
                                }
                                popupWindow2.dismiss();
                                popupWindow1.dismiss();
                                Toast.makeText(TeacherLessonCreateActivity.this,"Save to enable preview",Toast. LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("video/*");
                startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lessonNameStr = lessonName.getText().toString();
                if (lessonNameStr.matches("")) {
                    Toast.makeText(TeacherLessonCreateActivity.this, "Enter a lesson name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                StorageReference videoFile =
                        storageRef.child("videos/"+ Uri.parse(uri).getLastPathSegment());
                UploadTask uploadTask = videoFile.putFile(Uri.parse(uri));

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
                });

                // Save to Database
                boolean saved = false;
                if (newLesson) {
                    saved = service.saveLesson(lessonName.getText().toString(), metadata.toString(), Uri.parse(uri).getLastPathSegment(), teacherID, uri, classID);
                } else {
                    saved = service.updateLesson(lessonName.getText().toString(), metadata.toString(), Uri.parse(uri).getLastPathSegment(), teacherID, uri, classID);
                }

                if (saved) {
                    btn_preview.setEnabled(true);
                    lessonName.setFocusable(false);
                    newLesson = false;
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(lessonName.getWindowToken(), 0);
                } else {
                    lessonName.setFocusable(true);
                }
            }
        });

        Button btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.help_teacher_lesson_create, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                dimBehind(popupWindow);

                Button buttonCloseHelp = (Button) popupView.findViewById(R.id.close_help);
                buttonCloseHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(v -> {
            StudentHelper studentHelper = new StudentHelper(TeacherLessonCreateActivity.this);
            service.deleteLesson(lessonName.getText().toString(), teacherID, classID);
            studentHelper.deleteStudentCompetency(classID, lessonName.getText().toString(), "lesson");
            finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();
            if (selectedMediaUri.toString().contains("video")) {
                Toast.makeText(TeacherLessonCreateActivity.this, "Video Selected!", Toast.LENGTH_SHORT).show();
                uri = String.valueOf(selectedMediaUri);
                videoView.setVideoPath(String.valueOf(selectedMediaUri));
            } else if (selectedMediaUri.toString().contains("image")){
                Toast.makeText(TeacherLessonCreateActivity.this, "Image Selected!", Toast.LENGTH_SHORT).show();
                imageFile = new File(getRealPathFromURI(selectedMediaUri));
                imageAdded.setChecked(true);
            } else if (selectedMediaUri.toString().contains("audio")){
                Toast.makeText(TeacherLessonCreateActivity.this, "Audio Selected!", Toast.LENGTH_SHORT).show();
                audioFile = new File(getPathForAudio(selectedMediaUri));
                soundclipAdded.setChecked(true);
            }
            btn_add_activity.setEnabled(true);
        }
    }

    private String getPathForAudio(Uri uri) {
//        String wholeID = DocumentsContract.getDocumentId(uri);
//        String id = wholeID.split(":")[1];
//        String[] column = { MediaStore.Audio.Media.DATA };
//        String sel = MediaStore.Audio.Media._ID + "=?";
//
//        Cursor cursor = getContentResolver().
//                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                        column, sel, new String[]{ id }, null);
//
//        String filePath = "";
//
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//
//        cursor.close();
//        return filePath;

        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void createMcqQuestion(int count, String timestamp, String question, String ans1, String ans2, String ans3, String ans4, String answer, int counter) {
        try {
            JSONObject questionObj= new JSONObject();
            questionObj.put("Number", count);
            questionObj.put("Timestamp", timestamp);
            questionObj.put("Type", "MCQ");
            questionObj.put("Question", question);
            questionObj.put("Answer1", ans1);
            questionObj.put("Answer2", ans2);
            questionObj.put("Answer3", ans3);
            questionObj.put("Answer4", ans4);
            questionObj.put("Answer", answer);
            if (imageFile != null) questionObj.put("image", imageFile.getPath());
            if (audioFile != null) questionObj.put("audio", audioFile.getPath());
            createJsonObject(questionObj, counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createShortQuestion(int count, String timestamp, String question, String correct, int counter) {
        try {
            JSONObject questionObj= new JSONObject();
            questionObj.put("Number", count);
            questionObj.put("Timestamp", timestamp);
            questionObj.put("Type", "SHORT");
            questionObj.put("Question", question);
            questionObj.put("Answer", correct);
            if (imageFile != null) questionObj.put("image", imageFile.getPath());
            if (audioFile != null) questionObj.put("audio", audioFile.getPath());

            createJsonObject(questionObj, counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createJsonObject(JSONObject questionObject, int counter) {
        try {
            questionsArray.put(counter, questionObject);
            metadata.put("questions", questionsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commonMCQActivityButton (Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_mcq, null);

//                if (metadata == null) metadata = service.readJSON(lessonName.getText().toString());

                final PopupWindow childPopup = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                childPopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                dimBehind(childPopup);

                Button buttonSubmitMcq = (Button) popupView.findViewById(R.id.submit_mcq);
                EditText question = (EditText) popupView.findViewById(R.id.mcqQuestion);
                EditText answer_1 = (EditText) popupView.findViewById(R.id.ans_1);
                EditText answer_2 = (EditText) popupView.findViewById(R.id.ans_2);
                EditText answer_3 = (EditText) popupView.findViewById(R.id.ans_3);
                EditText answer_4 = (EditText) popupView.findViewById(R.id.ans_4);
                RadioGroup mcqAnswers = (RadioGroup) popupView.findViewById(R.id.mcq_ans_group);
                RadioButton btn_1 = (RadioButton) mcqAnswers.findViewById(R.id.btn_1);
                RadioButton btn_2 = (RadioButton) mcqAnswers.findViewById(R.id.btn_2);
                RadioButton btn_3 = (RadioButton) mcqAnswers.findViewById(R.id.btn_3);
                RadioButton btn_4 = (RadioButton) mcqAnswers.findViewById(R.id.btn_4);
                int metadataCounter = 0;
                JSONObject questionObject = null;
                boolean editingLesson = false;
                if(metadata != null) {
                    try {
                        for (int i = 0; i < questionsArray.length(); i++) {
                            String temp = getTimeString(Long.parseLong(((JSONObject) questionsArray.get(i)).getString("Timestamp")));
                            String btntext = button.getText().toString();
                            if(temp.equals(btntext)) {
                                questionObject = new JSONObject(questionsArray.get(i).toString());
                                metadataCounter = i;
                                question.append((questionObject.get("Question")).toString());
                                answer_1.append((questionObject.get("Answer1")).toString());
                                answer_2.append((questionObject.get("Answer2")).toString());
                                answer_3.append((questionObject.get("Answer3")).toString());
                                answer_4.append((questionObject.get("Answer4")).toString());
                                btn_1.setChecked((questionObject.getString("Answer")).equals("1"));
                                btn_2.setChecked((questionObject.getString("Answer")).equals("2"));
                                btn_3.setChecked((questionObject.getString("Answer")).equals("3"));
                                btn_4.setChecked((questionObject.getString("Answer")).equals("4"));
                                pausedPosition = Long.parseLong(questionObject.getString("Timestamp"));
                                editingLesson = true;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                RadioButton lowDifficulty = popupView.findViewById(R.id.lowDifficulty);
                RadioButton mediumDifficulty = popupView.findViewById(R.id.mediumDifficulty);
                RadioButton highDifficulty = popupView.findViewById(R.id.highDifficulty);
                lowDifficulty.setChecked(true);
                lowDifficulty.setEnabled(false);
                mediumDifficulty.setEnabled(false);
                highDifficulty.setEnabled(false);

                int finalMetadataCounter = metadataCounter;
                JSONObject finalQuestionObject = questionObject;
                boolean finalEditingLesson = editingLesson;
                buttonSubmitMcq.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(question.getText()) || TextUtils.isEmpty(answer_1.getText()) ||
                                TextUtils.isEmpty(answer_2.getText()) || TextUtils.isEmpty(answer_3.getText()) ||
                                TextUtils.isEmpty(answer_4.getText())) {
                            Toast.makeText(TeacherLessonCreateActivity.this, "Please complete all the fields.", Toast.LENGTH_SHORT).show();
                        } else {
                            String questionVar = question.getText().toString();
                            String answer1Var = answer_1.getText().toString();
                            String answer2Var = answer_2.getText().toString();
                            String answer3Var = answer_3.getText().toString();
                            String answer4Var = answer_4.getText().toString();
                            String correctAns = "1";

                            if (btn_1.isChecked()) correctAns = "1";
                            else if (btn_2.isChecked()) correctAns = "2";
                            else if (btn_3.isChecked()) correctAns = "3";
                            else if (btn_4.isChecked()) correctAns = "4";

                            if (imageAdded != null && !imageAdded.isChecked()) imageFile = null;

                            if (lowDifficulty.isChecked()) {
                                String pausedAt = Long.toString(pausedPosition);
                                mediumDifficulty.setChecked(true);
                                question.setText("");
                                answer_1.setText("");
                                answer_2.setText("");
                                answer_3.setText("");
                                answer_4.setText("");
                                btn_1.setChecked(true);
                                btn_2.setChecked(false);
                                btn_3.setChecked(false);
                                btn_4.setChecked(false);
                                if(finalEditingLesson) {
                                    try {
                                        questionCount = finalMetadataCounter;
                                        question.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Question"));
                                        answer_1.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer1"));
                                        answer_2.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer2"));
                                        answer_3.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer3"));
                                        answer_4.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer4"));
                                        btn_1.setChecked(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer").equals("1"));
                                        btn_2.setChecked(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer").equals("2"));
                                        btn_3.setChecked(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer").equals("3"));
                                        btn_4.setChecked(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer").equals("4"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                createMcqQuestion(questionCount, pausedAt, questionVar, answer1Var, answer2Var, answer3Var, answer4Var, correctAns, questionCount);
                            } else if (mediumDifficulty.isChecked()) {
                                JSONObject object = new JSONObject();
                                try {
                                    if (btn_1.isChecked()) correctAns = "1";
                                    else if (btn_2.isChecked()) correctAns = "2";
                                    else if (btn_3.isChecked()) correctAns = "3";
                                    else if (btn_4.isChecked()) correctAns = "4";
                                    object.put("Question", question.getText().toString());
                                    object.put("Answer1", answer_1.getText().toString());
                                    object.put("Answer2", answer_2.getText().toString());
                                    object.put("Answer3", answer_3.getText().toString());
                                    object.put("Answer4", answer_4.getText().toString());
                                    object.put("Answer", correctAns);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                addToJson("medium", object, questionCount);
                                question.setText("");
                                answer_1.setText("");
                                answer_2.setText("");
                                answer_3.setText("");
                                answer_4.setText("");
                                btn_1.setChecked(true);
                                btn_2.setChecked(false);
                                btn_3.setChecked(false);
                                btn_4.setChecked(false);

                                highDifficulty.setChecked(true);
                                buttonSubmitMcq.setText("Submit");
                                if(finalEditingLesson) {
                                    try {
                                        question.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Question"));
                                        answer_1.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer1"));
                                        answer_2.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer2"));
                                        answer_3.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer3"));
                                        answer_4.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer4"));
                                        btn_1.setChecked(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer").equals("1"));
                                        btn_2.setChecked(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer").equals("2"));
                                        btn_3.setChecked(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer").equals("3"));
                                        btn_4.setChecked(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer").equals("4"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (highDifficulty.isChecked()) {
                                JSONObject object = new JSONObject();
                                try {
                                    if (btn_1.isChecked()) correctAns = "1";
                                    else if (btn_2.isChecked()) correctAns = "2";
                                    else if (btn_3.isChecked()) correctAns = "3";
                                    else if (btn_4.isChecked()) correctAns = "4";
                                    object.put("Question", question.getText().toString());
                                    object.put("Answer1", answer_1.getText().toString());
                                    object.put("Answer2", answer_2.getText().toString());
                                    object.put("Answer3", answer_3.getText().toString());
                                    object.put("Answer4", answer_4.getText().toString());
                                    object.put("Answer", correctAns);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                addToJson("high", object, questionCount);
                                questionCount++;
                                if (!finalEditingLesson){
                                    questionCount = questionsArray.length();
                                    finishButton.setEnabled(true);
                                    questionAdded.setChecked(true);
                                }
                                childPopup.dismiss();
                                btn_preview.setEnabled(false);
                                btn_save.setVisibility(View.VISIBLE);
                                System.out.println(questionsArray);
                            }
                        }
                    }
                });
            }
        });
    }

    private void commonShortActivityButton (Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_short_ans, null);

//                if(metadata == null) metadata = service.readJSON(lessonName.getText().toString());

                final PopupWindow childPopup = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                childPopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                dimBehind(childPopup);

                Button buttonSubmitShort = (Button) popupView.findViewById(R.id.submit_short);
                EditText question = (EditText) popupView.findViewById(R.id.shortAnsQuestion);
                EditText answer = (EditText) popupView.findViewById(R.id.shortAnswer);
                int metadataCounter = 0;
                JSONObject questionObject = null;
                boolean editingLesson = false;

                if(metadata != null) {
                    try {
                        for (int i = 0; i < questionsArray.length(); i++) {
                            String temp = getTimeString(Long.parseLong(((JSONObject) questionsArray.get(i)).getString("Timestamp")));
                            String btntext = button.getText().toString();

                            if(temp.equals(btntext)) {
                                metadataCounter = i;
                                questionObject = new JSONObject(questionsArray.get(i).toString());
                                question.append((questionObject.get("Question")).toString());
                                answer.append((questionObject.get("Answer")).toString());
                                editingLesson = true;
                                pausedPosition = Long.parseLong(questionObject.getString("Timestamp"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                RadioButton lowDifficulty = popupView.findViewById(R.id.lowDifficulty);
                RadioButton mediumDifficulty = popupView.findViewById(R.id.mediumDifficulty);
                RadioButton highDifficulty = popupView.findViewById(R.id.highDifficulty);
                lowDifficulty.setChecked(true);
                lowDifficulty.setEnabled(false);
                mediumDifficulty.setEnabled(false);
                highDifficulty.setEnabled(false);

                int finalMetadataCounter = metadataCounter;
                JSONObject finalQuestionObject = questionObject;
                boolean finalEditingLesson = editingLesson;
                buttonSubmitShort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(question.getText()) || TextUtils.isEmpty(answer.getText())) {
                            Toast.makeText(TeacherLessonCreateActivity.this, "Please complete all the fields.", Toast.LENGTH_SHORT).show();
                        } else {

                            if (imageAdded != null && !imageAdded.isChecked()) imageFile = null;

                            if (lowDifficulty.isChecked()) {
                                String pausedAt = Long.toString(pausedPosition);
                                String questionVar = question.getText().toString();
                                String correctAnswer = answer.getText().toString();
                                question.setText("");
                                answer.setText("");
                                mediumDifficulty.setChecked(true);
                                if(finalEditingLesson) {
                                    try {
                                        questionCount = finalMetadataCounter;
                                        question.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Question"));
                                        answer.append(((JSONObject)(finalQuestionObject.get("medium"))).getString("Answer"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                createShortQuestion(questionCount, pausedAt, questionVar, correctAnswer, questionCount);
                            } else if (mediumDifficulty.isChecked()) {
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("Question", question.getText().toString());
                                    object.put("Answer", answer.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                addToJson("medium", object, questionCount);
                                question.setText("");
                                answer.setText("");
                                highDifficulty.setChecked(true);
                                buttonSubmitShort.setText("Submit");
                                if(finalEditingLesson) {
                                    try {
                                        question.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Question"));
                                        answer.append(((JSONObject)(finalQuestionObject.get("high"))).getString("Answer"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (highDifficulty.isChecked()) {
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("Question", question.getText().toString());
                                    object.put("Answer", answer.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                addToJson("high", object, questionCount);
                                questionCount++;
                                if(!finalEditingLesson){
                                    questionCount = questionsArray.length();
                                    finishButton.setEnabled(true);
                                    questionAdded.setChecked(true);
                                }
                                childPopup.dismiss();
                                btn_preview.setEnabled(false);
                                btn_save.setVisibility(View.VISIBLE);
                                System.out.println(questionsArray);
                            }
                        }
                    }
                });
            }
        });
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.5f;
        wm.updateViewLayout(container, p);
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
            .append(String.format("%02d", hours))
            .append(":")
            .append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds));

        return buf.toString();
    }

    private void addToJson(String difficulty, JSONObject object, int counter) {
        try {
            if (questionsArray.length() == 0) {
                ((JSONObject) questionsArray.get(0))
                        .put(difficulty, object);
            } else {
                ((JSONObject) questionsArray.get(counter))
                        .put(difficulty, object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}