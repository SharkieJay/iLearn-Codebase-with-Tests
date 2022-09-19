package com.example.mycorrectapplication.database.lesson;

import android.provider.BaseColumns;

public class LessonContract {

    private LessonContract() {
    }

    public static class Lesson implements BaseColumns {
        public static final String TABLE_NAME = "lessons";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_METADATA = "metadata";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_TEACHER = "teacher";
        public static final String COLUMN_NAME_LOCAL_LINK = "local_link";
        public static final String COLUMN_NAME_CLASSID = "classroom";
    }

    public static final String SQL_CREATE_LESSON_TABLE =
            "CREATE TABLE " + LessonContract.Lesson.TABLE_NAME + " (" +
                    Lesson._ID + " INTEGER PRIMARY KEY," +
                    Lesson.COLUMN_NAME_NAME + " TEXT," +
                    Lesson.COLUMN_NAME_METADATA + " TEXT," +
                    Lesson.COLUMN_NAME_TEACHER + " TEXT," +
                    Lesson.COLUMN_NAME_LINK + " TEXT," +
                    Lesson.COLUMN_NAME_LOCAL_LINK + " TEXT," +
                    Lesson.COLUMN_NAME_CLASSID + " TEXT)";

    public static final String SQL_DELETE_LESSON_ENTRIES =
            "DROP TABLE IF EXISTS " + LessonContract.Lesson.TABLE_NAME;
}
