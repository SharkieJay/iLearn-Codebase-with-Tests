package com.example.mycorrectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.student.StudentHelper;

public class StudentRegisterActivity extends AppCompatActivity {

    EditText name;
    EditText username;
    EditText password;
    EditText password_re;
    Button btn_register;
    Button btn_learning_style;
    String learningStyle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_register);

        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password_re = (EditText) findViewById(R.id.password_re);
        btn_register = (Button) findViewById(R.id.btn_register);

        //Add onClickListener
        btn_learning_style = (Button) findViewById(R.id.btn_learning_style);
        btn_learning_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(StudentRegisterActivity.this, StudentRegisterSetLearningStyleActivity.class);
                startActivityForResult(intentPreview, 0);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerStudent()) {
                    Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intentPreview = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentPreview);
                }
            }
        });
    }
    private boolean registerStudent() {
        StudentHelper service = new StudentHelper(this);
        if (learningStyle != null) {
            return service.registerStudent(
                    name.getText().toString(),
                    username.getText().toString(),
                    password.getText().toString(),
                    learningStyle
            );
        } else {
            Toast.makeText(getApplicationContext(), "Find your Learning Style to proceed.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            learningStyle = data.getStringExtra("learningStyle");
            btn_learning_style.setText(learningStyle + " Learner");
            btn_learning_style.setEnabled(false);
        }
    }
}
