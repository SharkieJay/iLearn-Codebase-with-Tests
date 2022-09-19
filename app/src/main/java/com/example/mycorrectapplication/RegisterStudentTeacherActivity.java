package com.example.mycorrectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterStudentTeacherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_student_teacher);

        Button student = (Button) findViewById(R.id.btn_register_student);
        Button teacher = (Button) findViewById(R.id.btn_register_teacher);

        student.setOnClickListener(v -> {
            Intent intentPreview = new Intent(RegisterStudentTeacherActivity.this, StudentRegisterActivity.class);
            startActivity(intentPreview);
        });

        teacher.setOnClickListener(v -> {
            Intent intentPreview = new Intent(RegisterStudentTeacherActivity.this, RegisterActivity.class);
            startActivity(intentPreview);
        });
    }
}
