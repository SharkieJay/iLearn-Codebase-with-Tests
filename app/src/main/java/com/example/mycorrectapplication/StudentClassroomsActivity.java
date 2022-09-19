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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentClassroomsActivity extends AppCompatActivity {
    private StudentHelper service;
    private final String SHARED_PREF = "sharedPrefs";
    private final String STUDENT_ID = "studentId";
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_classrooms);
        Bundle bundle = getIntent().getExtras();
        studentId = bundle.getString("studentId");
        service = new StudentHelper(this);
        try {
            getAllClasses();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button btn_import = findViewById(R.id.btn_import);
//        btn_import.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    private void getAllClasses() throws JSONException {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        JSONArray classes = service.getClassrooms(sharedPreferences.getString(STUDENT_ID, null));
        JSONObject classroom;

        // Adding layout
        LinearLayout ll = (LinearLayout) findViewById(R.id.scroll_classrooms);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 20, 0, 0);
        params.gravity = Gravity.CENTER;
        TextView txt = (TextView) findViewById(R.id.txt_none);

        // Adding lessons to layout
        if (classes != null && classes.length() != 0) {
            txt.setVisibility(View.GONE);
            Resources r = getResources();
            // Converting DP to pixels to set button width
//            int px =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
//                    ,239
//                    ,r.getDisplayMetrics());

            for (int i=0; i < classes.length(); i++) {
                classroom = classes.getJSONObject(i);
                String className = classroom.getString("classroom");

                String name = "class" + classroom.getString("classroom_id");
                int resID = getResources().getIdentifier(name, "id", getPackageName());

                Button btn_activity = new Button(getApplicationContext());
                btn_activity.setId(resID);
                btn_activity.setText(className);
                btn_activity.setBackgroundColor(getResources().getColor(R.color.Green_Btn));
                btn_activity.setLayoutParams(params);
                JSONObject finalLesson = classroom;
                btn_activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentPreview = new Intent(StudentClassroomsActivity.this, StudentLessonsActivity.class);
                        intentPreview.putExtra("json",finalLesson.toString());
                        intentPreview.putExtra("className",className);
                        intentPreview.putExtra("studentId",studentId);
                        startActivity(intentPreview);
                    }
                });
                ll.addView(btn_activity);
            }
        } else {
            txt.setVisibility(View.VISIBLE);
        }
    }
}
