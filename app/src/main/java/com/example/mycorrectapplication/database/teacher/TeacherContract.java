package com.example.mycorrectapplication.database.teacher;

import android.provider.BaseColumns;

public class TeacherContract {

    private TeacherContract() {
    }

    public static class Teacher implements BaseColumns {
        public static final String TABLE_NAME = "teachers";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_USERNAME = "username";
    }

    public static final String SQL_CREATE_TEACHER_TABLE =
            "CREATE TABLE " + Teacher.TABLE_NAME + " (" +
                    Teacher._ID + " INTEGER PRIMARY KEY," +
                    Teacher.COLUMN_NAME_PASSWORD + " TEXT," +
                    Teacher.COLUMN_NAME_NAME + " TEXT," +
                    Teacher.COLUMN_NAME_USERNAME + " TEXT)";

    public static final String SQL_DELETE_TEACHER_ENTRIES =
            "DROP TABLE IF EXISTS " + Teacher.TABLE_NAME;
}
