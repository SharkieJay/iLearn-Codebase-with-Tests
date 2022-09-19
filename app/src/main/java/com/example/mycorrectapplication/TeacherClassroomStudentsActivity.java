package com.example.mycorrectapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.DataService;
import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;
import com.example.mycorrectapplication.database.student.StudentHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherClassroomStudentsActivity extends AppCompatActivity {
    private DataService service;
    private String studentID;
    private String className;
    private String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_classroom_students);

        service = new DataService(this);
        Bundle bundle = getIntent().getExtras();

        className = bundle.getString("classname");
        classID = bundle.getString("classID");

        Button add_student = (Button) findViewById(R.id.btn_add_student);
        add_student.setOnClickListener(v -> {
            addStudent();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getStudents();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getStudents() throws JSONException {
        ClassroomHelper service = new ClassroomHelper(this);
        StudentHelper studentHelper = new StudentHelper(this);
        JSONArray students = service.getStudents(classID);
        LinearLayout ll = (LinearLayout) findViewById(R.id.students_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView txt = (TextView) findViewById(R.id.txt_none);
        ll.removeAllViews();
        if (students != null && students.length() != 0) {
            txt.setVisibility(View.GONE);

            for (int i = 0; i < students.length(); i++) {
                String name = "student" + i;
                String studentID = students.getString(i);
                String studentName = studentHelper.getStudentName(studentID);
                int resID = getResources().getIdentifier(name, "id", getPackageName());
                Button btn_activity = new Button(getApplicationContext());
                btn_activity.setId(resID);
                btn_activity.setText(studentName);
                btn_activity.setBackgroundColor(getResources().getColor(R.color.Green_Btn));
                params.setMargins(0,0,0,10);
                btn_activity.setLayoutParams(params);
//                btn_activity.setWidth(px);
//                JSONObject finalLesson = lesson;
                btn_activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentPreview = new Intent(TeacherClassroomStudentsActivity.this, TeacherClassroomStudentViewActivity.class);
                        intentPreview.putExtra("studentName", studentName);
                        intentPreview.putExtra("studentID", studentID);
                        intentPreview.putExtra("classID", classID);
                        startActivity(intentPreview);
                    }
                });
                ll.addView(btn_activity);
            }
        } else {
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void addStudent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Student Username");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                studentID = input.getText().toString();
                service.addStudentToClassroom(studentID, className, classID);
                onResume();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
