package com.example.mycorrectapplication;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;
import com.example.mycorrectapplication.database.student.StudentHelper;

import org.json.JSONArray;
import org.json.JSONException;

public class TeacherClassroomStudentViewActivity extends AppCompatActivity {
    private String studName;
    private String studentID;
    private String classID;
    private StudentHelper studentHelper;
    JSONArray competencyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_classroom_student_view);

        Bundle bundle = getIntent().getExtras();
        studName = bundle.getString("studentName");
        studentID = bundle.getString("studentID");
        classID = bundle.getString("classID");
        studentHelper = new StudentHelper(TeacherClassroomStudentViewActivity.this);
        try {
            initObjects();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initObjects() throws JSONException {
        String learningStyle = studentHelper.getLearningStyle(studentID);
        TextView studentName = findViewById(R.id.btn_student_name);
        studentName.setText(studName);
        TextView txt_learn_style = findViewById(R.id.txt_learn_style);
        txt_learn_style.setText(learningStyle);
        competencyArray = studentHelper.getCompetency(studentID);

        Button btn_remove = findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassroomHelper classroomHelper = new ClassroomHelper(getApplicationContext());
                classroomHelper.removeStudent(studentID, classID);
                Toast.makeText(getApplicationContext(), "Student Removed!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        LinearLayout ll = findViewById(R.id.scroll_ll);
        TextView txt = findViewById(R.id.txt_none);
        ll.removeAllViews();

        boolean classroomAvailable = false;
        if (competencyArray != null && competencyArray.length() != 0) {
            txt.setVisibility(View.GONE);
            for (int i = 0; i < competencyArray.length(); i++) {
                String name = "tv" + i;
                int resID = getResources().getIdentifier(name, "id", getPackageName());
                TextView competencyView = new TextView(this);
                competencyView.setId(resID);
                if (competencyArray.getJSONObject(i).getString("classroom_id").equals(classID)) {
                    classroomAvailable = true;
                    competencyView.setText(competencyArray.getJSONObject(i).getString("lesson_id") + ":      " +
                            competencyArray.getJSONObject(i).getString("competency"));
                    competencyView.setTextSize(15);
                    competencyView.setTypeface(competencyView.getTypeface(), Typeface.BOLD);
                    competencyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.addView(competencyView);
                }
            }
        } else {
            txt.setVisibility(View.VISIBLE);
        }
        if (classroomAvailable == false) {
            txt.setVisibility(View.VISIBLE);
        }
    }
}
