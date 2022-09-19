package com.example.mycorrectapplication.database.classrooms;

import android.provider.BaseColumns;


public class ClassroomContract {
    private ClassroomContract() {
    }

    public static class Classroom implements BaseColumns {
        public static final String TABLE_NAME = "classrooms";
        public static final String COLUMN_NAME_ID = "class_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STUDENTS = "students";
        public static final String COLUMN_NAME_LESSONS = "lessons";
        public static final String COLUMN_NAME_TEACHER_ID = "teacher";
    }

    public static final String SQL_CREATE_CLASSROOM_TABLE =
            "CREATE TABLE " + Classroom.TABLE_NAME + " (" +
                    Classroom._ID + " INTEGER PRIMARY KEY," +
                    Classroom.COLUMN_NAME_ID + " TEXT," +
                    Classroom.COLUMN_NAME_NAME + " TEXT," +
                    Classroom.COLUMN_NAME_STUDENTS + " TEXT," +
                    Classroom.COLUMN_NAME_TEACHER_ID + " TEXT," +
                    Classroom.COLUMN_NAME_LESSONS + " TEXT)";

    public static final String SQL_DELETE_CLASSROOM_ENTRIES =
            "DROP TABLE IF EXISTS " + Classroom.TABLE_NAME;
}
