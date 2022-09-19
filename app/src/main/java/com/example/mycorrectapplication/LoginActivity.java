package com.example.mycorrectapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.student.StudentHelper;
import com.example.mycorrectapplication.database.teacher.TeacherHelper;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button btn_login;
    Button btn_register;

    private StudentHelper studentHelper;
    private TeacherHelper teacherHelper;
    private final String SHARED_PREF = "sharedPrefs";
    private final String TEACHER_ID = "teacherId";
    private final String STUDENT_ID = "studentId";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        teacherHelper = new TeacherHelper(LoginActivity.this);
        studentHelper = new StudentHelper(LoginActivity.this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_username = username.getText().toString();
                String str_password = password.getText().toString();

                if (!(str_username.isEmpty() || str_password.isEmpty())){
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String actualPwTeacher = teacherHelper.getTeacher(str_username);
                    String actualPwStudent = studentHelper.getStudent(str_username);

                    if (str_password.equals(actualPwTeacher)) {
                        editor.putString(TEACHER_ID, str_username);
                        editor.apply();
                        Intent intentPreview = new Intent(getApplicationContext(), TeacherHomeActivity.class);
                        startActivity(intentPreview);
                    } else if (str_password.equals(actualPwStudent)) {
                        editor.putString(STUDENT_ID, str_username);
                        editor.apply();
                        Intent intentPreview = new Intent(getApplicationContext(), StudentHomeActivity.class);
                        startActivity(intentPreview);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid credentials!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Incomplete fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), RegisterStudentTeacherActivity.class);
                startActivity(intentPreview);
            }
        });
    }

    @Override
    public void onBackPressed() {}
}
