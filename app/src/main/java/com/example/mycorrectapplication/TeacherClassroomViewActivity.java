package com.example.mycorrectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherClassroomViewActivity extends AppCompatActivity {
    private final String SHARED_PREF = "sharedPrefs";
    private final String TEACHER_ID = "teacherId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_classroom_view);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try {
            getClassrooms();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getClassrooms() throws JSONException {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String teacherID = sharedPreferences.getString(TEACHER_ID, "");
        ClassroomHelper service = new ClassroomHelper(this);
        JSONArray classrooms = service.getClassrooms(teacherID);
        LinearLayout ll = (LinearLayout) findViewById(R.id.classrooms_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView txt = findViewById(R.id.txt_none);
        ll.removeAllViews();
        JSONObject classroom;

        if (classrooms.length() != 0) {
            txt.setVisibility(View.GONE);

            for (int i = 0; i < classrooms.length(); i++) {
                classroom = classrooms.getJSONObject(i);

                String name = "student" + i;
                int resID = getResources().getIdentifier(name, "id", getPackageName());

                Button btn_activity = new Button(getApplicationContext());
                btn_activity.setId(resID);
                btn_activity.setText(classroom.getString("classname"));
                btn_activity.setBackgroundColor(getResources().getColor(R.color.Green_Btn));
                params.setMargins(0,0,0,10);
                btn_activity.setLayoutParams(params);

                JSONObject finalClass = classroom;
                btn_activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editLesson(finalClass);
                    }
                });
                ll.addView(btn_activity);
            }
        }
        else {
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void editLesson(JSONObject classroom) {
        Intent intentPreview = new Intent(getApplicationContext(), TeacherClassroomCreateActivity.class);
        intentPreview.putExtra("classroom", classroom.toString());
        startActivity(intentPreview);
    }

}
