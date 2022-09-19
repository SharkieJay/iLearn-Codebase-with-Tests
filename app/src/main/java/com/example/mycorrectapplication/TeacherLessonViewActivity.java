package com.example.mycorrectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.teacher.TeacherHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherLessonViewActivity extends AppCompatActivity {
    private TeacherHelper service;
    private String classID;
    private String classroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_lesson_view);
        Bundle bundle = getIntent().getExtras();

        classID = bundle.getString("classID");
        classroom = bundle.getString("classname");
        initObjects();

        service = new TeacherHelper(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try {
            getAllLessons();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initObjects() {
        TextView classname = (TextView) findViewById(R.id.classroomName);
        classname.setText(classroom);
        Button btn_create_lesson = (Button) findViewById(R.id.btn_create_lesson);
        btn_create_lesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(TeacherLessonViewActivity.this, TeacherLessonCreateActivity.class);
                intentPreview.putExtra("classID", classID);
                startActivity(intentPreview);
            }
        });
    }

    private void getAllLessons() throws JSONException {
        JSONArray lessons = service.getLessons(classID);
        JSONObject lesson;

        // Adding layout
        LinearLayout ll = (LinearLayout) findViewById(R.id.scroll_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 10);
        TextView txt = (TextView) findViewById(R.id.txt_none);
        ll.removeAllViews();

        // Adding lessons to layout
        if (lessons.length() != 0) {
            txt.setVisibility(View.GONE);
            for (int i=0; i < lessons.length(); i++) {
                lesson = lessons.getJSONObject(i);

                String name = "lesson" + lesson.getString("name");
                int resID = getResources().getIdentifier(name, "id", getPackageName());

                Button btn_activity = new Button(getApplicationContext());
                btn_activity.setId(resID);
                btn_activity.setText(lesson.getString("name"));
                btn_activity.setBackgroundColor(getResources().getColor(R.color.Green_Btn));
                btn_activity.setLayoutParams(params);
                JSONObject finalLesson = lesson;
                btn_activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLesson(finalLesson);
                    }
                });
                ll.addView(btn_activity);
            }
        }
        else {
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void editLesson(JSONObject lesson) {
        Intent intentPreview = new Intent(TeacherLessonViewActivity.this, TeacherLessonCreateActivity.class);
        intentPreview.putExtra("json",lesson.toString());
        intentPreview.putExtra("classID",classID);
        startActivity(intentPreview);
    }
}
