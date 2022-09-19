package com.example.mycorrectapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.student.StudentHelper;

public class StudentHomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private final String SHARED_PREF = "sharedPrefs";
    private final String STUDENT_ID = "studentId";
    private String studentId;
    private StudentHelper studentHelper;
    private String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_home);
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        studentId = sharedPreferences.getString(STUDENT_ID, "");
        studentHelper = new StudentHelper(StudentHomeActivity.this);
        initObjects();
    }

    private void initObjects() {
        TextView title = (TextView) findViewById(R.id.title);
        studentName = studentHelper.getStudentName(studentId);
        title.append(" " + studentName + "!");
        Button btn_classrooms = (Button) findViewById(R.id.btn_classrooms);
        btn_classrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), StudentClassroomsActivity.class);
                intentPreview.putExtra("studentId", studentId);
                startActivity(intentPreview);
            }
        });

        Button btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        Button btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.help_student_home, null);
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
    }

    private void LogOut() {
                AlertDialog .Builder builder = new AlertDialog.Builder(StudentHomeActivity.this);
                builder.setTitle("Logout Confirmation").setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
    }

    @Override
    public void onBackPressed() {}
}
