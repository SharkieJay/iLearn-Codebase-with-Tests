package com.example.mycorrectapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycorrectapplication.database.teacher.TeacherHelper;

public class TeacherHomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private final String SHARED_PREF = "sharedPrefs";
    private final String TEACHER_ID = "teacherId";
    private String teacherId;
    private String teacherName;
    private TeacherHelper teacherHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_home);
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        teacherId = sharedPreferences.getString(TEACHER_ID, "");
        teacherHelper = new TeacherHelper(TeacherHomeActivity.this);
        initObjects();
    }

    private void initObjects(){
        TextView title = (TextView) findViewById(R.id.title);
        teacherName = teacherHelper.getTeacherName(teacherId);
        title.append(" " + teacherName + "!");
        Button btn_classrooms = (Button) findViewById(R.id.btn_classrooms);
        btn_classrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPreview = new Intent(getApplicationContext(), TeacherClassroomActivity.class);
                intentPreview.putExtra("teacherId", teacherId);
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
                View popupView = inflater.inflate(R.layout.help_teacher_home, null);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherHomeActivity.this);
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
