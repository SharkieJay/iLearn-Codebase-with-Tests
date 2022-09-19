package com.example.mycorrectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;
import com.example.mycorrectapplication.database.lesson.LessonHelper;
import com.example.mycorrectapplication.database.student.StudentHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TeacherClassroomCreateActivity extends AppCompatActivity {
    private final String SHARED_PREF = "sharedPrefs";
    private final String TEACHER_ID = "teacherId";
    private EditText classname;
    private SharedPreferences sharedPreferences;
    private String teacherID;
    private JSONObject classroom;
    private String editClassname;
    private ClassroomHelper service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_classroom_create);
        Bundle bundle = getIntent().getExtras();
        service = new ClassroomHelper(this);
        if (bundle != null){
            try {
                classroom = new JSONObject(bundle.getString("classroom"));
                editClassname = classroom.getString("classname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initObjects();
    }

    private void initObjects() {
        classname = (EditText) findViewById(R.id.class_name);
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        teacherID = sharedPreferences.getString(TEACHER_ID, "");
        Button btn_lessons = (Button) findViewById(R.id.btn_lessons);
        btn_lessons.setVisibility(View.GONE);
        btn_lessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherLessonViewActivity.class);
                intentPreview.putExtra("classID", teacherID + "_" + classname.getText().toString());
                intentPreview.putExtra("classname", classname.getText().toString());
                startActivity(intentPreview);
            }
        });

        Button btn_students = (Button) findViewById(R.id.btn_students);
        btn_students.setVisibility(View.GONE);
        btn_students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherClassroomStudentsActivity.class);
                intentPreview.putExtra("classID", teacherID + "_" + classname.getText().toString());
                intentPreview.putExtra("classname", classname.getText().toString());
                startActivity(intentPreview);
            }
        });

        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setVisibility(View.GONE);
        btn_delete.setOnClickListener(v -> {
            StudentHelper studentHelper = new StudentHelper(TeacherClassroomCreateActivity.this);
            service.deleteClassroom(teacherID + "_" + classname.getText().toString());
            studentHelper.deleteStudentCompetency(teacherID + "_" + classname.getText().toString(), null, "classroom");
            finish();
        });

        Button btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setVisibility(View.GONE);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.help_teacher_classroom_create, null);
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

        Button btn_export = findViewById(R.id.btn_export);
        btn_export.setVisibility(View.GONE);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LessonHelper lessonHelper = new LessonHelper(TeacherClassroomCreateActivity.this);
                JSONArray array = lessonHelper.exportClassroom(teacherID, classname.getText().toString());

                File path = Environment.getExternalStorageDirectory();
                File file = new File(path, "Documents/ilearn.json");
                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    stream.write(array.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(getApplicationContext(), "Successfully exported to 'Documents' folder", Toast.LENGTH_LONG).show();
            }
        });

        Button btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(v -> {
            String classnameStr = classname.getText().toString();
            if (classnameStr.matches("")) {
                Toast.makeText(this, "Enter a classroom name!", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean saved = service.saveClassroom(classname.getText().toString(), teacherID);
            if (saved) {
                btn_lessons.setVisibility(View.VISIBLE);
                btn_students.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                btn_delete.setVisibility(View.VISIBLE);
                btn_export.setVisibility(View.VISIBLE);
                btn_help.setVisibility(View.VISIBLE);
                classname.setFocusable(false);
            }
        });

        if (classroom != null) {
            classname.setText(editClassname);
            classname.setFocusable(false);
            btn_save.setVisibility(View.GONE);
            btn_lessons.setVisibility(View.VISIBLE);
            btn_students.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_export.setVisibility(View.VISIBLE);
            btn_help.setVisibility(View.VISIBLE);
        }
    }
}
