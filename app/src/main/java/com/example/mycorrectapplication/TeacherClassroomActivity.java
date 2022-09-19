package com.example.mycorrectapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.lesson.LessonHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TeacherClassroomActivity extends AppCompatActivity {

    String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_classroom);
        Bundle bundle = getIntent().getExtras();
        teacherId = bundle.getString("teacherId");
        initObjects();
    }

    private void initObjects() {
        Button btn_create_classroom = (Button) findViewById(R.id.btn_create_classroom);
        btn_create_classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherClassroomCreateActivity.class);
                startActivity(intentPreview);
            }
        });

        Button btn_view_classrooms = (Button) findViewById(R.id.btn_view_classrooms);
        btn_view_classrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherClassroomViewActivity.class);
                startActivity(intentPreview);
            }
        });

        Button btn_import = findViewById(R.id.btn_import);
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pickIntent.setType("application/json");
                startActivityForResult(pickIntent,1);
            }
        });

        Button btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.help_teacher_classroom, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                TeacherLessonCreateActivity activity = new TeacherLessonCreateActivity();
                activity.dimBehind(popupWindow);

                Button buttonCloseHelp = (Button) popupView.findViewById(R.id.close_help);
                buttonCloseHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            StringBuilder sb = null;
            try(InputStream inputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/Documents", "ilearn.json"))) {
                sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = new JSONArray(sb.toString());
                System.out.println("JsonArr: " + jsonArray);
                LessonHelper lessonHelper = new LessonHelper(TeacherClassroomActivity.this);
                lessonHelper.importClassroom(jsonArray, teacherId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(0);
            cursor.close();
        }
        return result;
    }
}
