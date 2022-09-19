package com.example.mycorrectapplication.database.student;

import android.provider.BaseColumns;

public class StudentContract {
    private StudentContract() {
    }

    public static class Student implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_COMPETENCY = "competency";
        public static final String COLUMN_NAME_CLASSROOMS = "classrooms";
        public static final String COLUMN_NAME_LEARNINGSTYLE = "learningStyle";
        public static final String COLUMN_NAME_COMPLETEDLESSONS = "completedLessons";
    }

    public static final String SQL_CREATE_STUDENT_TABLE =
            "CREATE TABLE " + Student.TABLE_NAME + " (" +
                    Student._ID + " INTEGER PRIMARY KEY," +
                    Student.COLUMN_NAME_ID + " TEXT," +
                    Student.COLUMN_NAME_USERNAME + " TEXT," +
                    Student.COLUMN_NAME_NAME + " TEXT," +
                    Student.COLUMN_NAME_PASSWORD + " TEXT," +
                    Student.COLUMN_NAME_COMPETENCY + " TEXT," +
                    Student.COLUMN_NAME_CLASSROOMS + " TEXT, " +
                    Student.COLUMN_NAME_LEARNINGSTYLE + " TEXT, " +
                    Student.COLUMN_NAME_COMPLETEDLESSONS + " TEXT)";

    public static final String SQL_DELETE_STUDENT_ENTRIES =
            "DROP TABLE IF EXISTS " + Student.TABLE_NAME;
}


