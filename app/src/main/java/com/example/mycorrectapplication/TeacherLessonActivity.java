package com.example.mycorrectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherLessonActivity extends AppCompatActivity {
    private String classID;
    private String classname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_lesson);
        initObjects();
    }

    private void initObjects() {
        Button btn_create_new_lesson = (Button) findViewById(R.id.btn_create_new_lesson);
        btn_create_new_lesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherLessonCreateActivity.class);
                startActivity(intentPreview);
            }
        });

        Button btn_view_lesson = (Button) findViewById(R.id.btn_view_lesson);
        btn_view_lesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherLessonViewActivity.class);
                intentPreview.putExtra("classID", classID);
                intentPreview.putExtra("classname", classname);
                startActivity(intentPreview);
            }
        });


    }
}

