package com.example.mycorrectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.teacher.TeacherHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText name;
    EditText username;
    EditText password;
    EditText password_re;
    Button btn_register;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password_re = (EditText) findViewById(R.id.password_re);
        btn_register = (Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerTeacher()) {
                    Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intentPreview = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intentPreview);
                }
            }
        });
    }

    public boolean registerTeacher() {
        TeacherHelper service = new TeacherHelper(RegisterActivity.this);
        return service.registerTeacher(
                name.getText().toString(),
                username.getText().toString(),
                password.getText().toString());
    }
}
