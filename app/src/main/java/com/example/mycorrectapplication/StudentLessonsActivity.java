package com.example.mycorrectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.student.StudentHelper;
import com.example.mycorrectapplication.database.teacher.TeacherHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentLessonsActivity extends AppCompatActivity {
    private TeacherHelper teacherHelper;
    private StudentHelper studentHelper;
    private String classID = "";
    private String className;
    private String studentId;
    private JSONObject json;
    private Button clickedButton;
    JSONObject lesson;
    JSONArray completedLessonsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_lessons);
        Bundle bundle = getIntent().getExtras();

        try {
            json = new JSONObject(bundle.getString("json"));
            classID = json.getString("classroom_id");
            className = bundle.getString("className");
            studentId = bundle.getString("studentId");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        initObjects();
    }

    private void initObjects() {
        TextView classname = (TextView) findViewById(R.id.class_name);
        classname.setText(className);
        teacherHelper = new TeacherHelper(StudentLessonsActivity.this);
        studentHelper = new StudentHelper(StudentLessonsActivity.this);
        try {
            getAllLessons();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAllLessons() throws JSONException {
        JSONArray lessons = teacherHelper.getLessons(classID);

        // Adding layout
        LinearLayout ll = (LinearLayout) findViewById(R.id.student_lessons_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 20, 0, 0);
        params.gravity = Gravity.CENTER;
        TextView txt = (TextView) findViewById(R.id.txt_none);

        // Adding lessons to layout
        if (lessons.length() != 0) {
            txt.setVisibility(View.GONE);
            Resources r = getResources();
            // Converting DP to pixels to set button width
            int px =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                    ,239
                    ,r.getDisplayMetrics());

            completedLessonsArray = studentHelper.getCompletedLessons(studentId);
            for (int i=0; i < lessons.length(); i++) {
                lesson = lessons.getJSONObject(i);
                String name = "lesson" + lesson.getString("name");
                int resID = getResources().getIdentifier(name, "id", getPackageName());

                Button btn_activity = new Button(getApplicationContext());
                btn_activity.setId(resID);
                btn_activity.setAllCaps(false);
                btn_activity.setText(lesson.getString("name"));
                btn_activity.setBackgroundColor(getResources().getColor(R.color.Green_Btn));
                btn_activity.setLayoutParams(params);
                for (int j = 0; j < completedLessonsArray.length(); j++) {
                    String completedLessonId = completedLessonsArray.getJSONObject(j).getString("lesson_id");
                    String completedClassroom = completedLessonsArray.getJSONObject(j).getString("classroom_id");
                    if (completedClassroom.equals(classID) && completedLessonId.equals(lesson.getString("name"))) {
                        btn_activity.setEnabled(false);
                        btn_activity.append(": Completed");
                    }
                }
                //btn_activity.setWidth(px);
                JSONObject finalLesson = lesson;
                btn_activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        previewLesson(finalLesson);
                        clickedButton = btn_activity;
                    }
                });
                ll.addView(btn_activity);
            }
        } else {
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void previewLesson(JSONObject lesson) {
        Intent intent = new Intent(getApplicationContext(), NewPreviewActivity.class);
        try {
            intent.putExtra("uri", lesson.getString("local_link"));
            intent.putExtra("classId", classID);
            intent.putExtra("lessonName", lesson.getString("name"));
            intent.putExtra("studentId", studentId);
            startActivityForResult(intent, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean lessonCompleted = data.getBooleanExtra("lessonCompleted", false);
            String completedLessonName = data.getStringExtra("completedLessonName");
            if (lessonCompleted) {
                clickedButton.setEnabled(false);
                clickedButton.append(": Completed");
                try {
                    studentHelper.updateCompletedLessons(studentId, classID, completedLessonName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
