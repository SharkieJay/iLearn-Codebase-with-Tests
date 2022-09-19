package com.example.mycorrectapplication.database;

import static com.example.mycorrectapplication.database.classrooms.ClassroomContract.SQL_CREATE_CLASSROOM_TABLE;
import static com.example.mycorrectapplication.database.classrooms.ClassroomContract.SQL_DELETE_CLASSROOM_ENTRIES;
import static com.example.mycorrectapplication.database.lesson.LessonContract.SQL_CREATE_LESSON_TABLE;
import static com.example.mycorrectapplication.database.lesson.LessonContract.SQL_DELETE_LESSON_ENTRIES;
import static com.example.mycorrectapplication.database.student.StudentContract.SQL_CREATE_STUDENT_TABLE;
import static com.example.mycorrectapplication.database.student.StudentContract.SQL_DELETE_STUDENT_ENTRIES;
import static com.example.mycorrectapplication.database.teacher.TeacherContract.SQL_CREATE_TEACHER_TABLE;
import static com.example.mycorrectapplication.database.teacher.TeacherContract.SQL_DELETE_TEACHER_ENTRIES;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "iLearn.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STUDENT_TABLE);
        db.execSQL(SQL_CREATE_TEACHER_TABLE);
        db.execSQL(SQL_CREATE_LESSON_TABLE);
        db.execSQL(SQL_CREATE_CLASSROOM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STUDENT_ENTRIES);
        db.execSQL(SQL_DELETE_TEACHER_ENTRIES);
        db.execSQL(SQL_DELETE_LESSON_ENTRIES);
        db.execSQL(SQL_DELETE_CLASSROOM_ENTRIES);
        onCreate(db);
    }
}
