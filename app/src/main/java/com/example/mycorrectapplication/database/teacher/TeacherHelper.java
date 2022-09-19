package com.example.mycorrectapplication.database.teacher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.example.mycorrectapplication.database.DatabaseHelper;
import com.example.mycorrectapplication.database.lesson.LessonContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherHelper {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase readableDB;
    private SQLiteDatabase writableDB;
    private Context context = null;

    public TeacherHelper(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public String getTeacher(String username) {
        String[] projection = {
                BaseColumns._ID,
                TeacherContract.Teacher.COLUMN_NAME_PASSWORD
        };

        // Filter results from Lesson name
        String selection = TeacherContract.Teacher.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = readableDB.query(
                TeacherContract.Teacher.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        String pw = "";
        while(cursor.moveToNext()) {
            pw = cursor.getString(1);
        }
        cursor.close();
        return pw;
    }

    public String getTeacherName(String username) {
        String[] projection = {
                BaseColumns._ID,
                TeacherContract.Teacher.COLUMN_NAME_NAME
        };

        // Filter results from Lesson name
        String selection = TeacherContract.Teacher.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = readableDB.query(
                TeacherContract.Teacher.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        String tn = "";
        while(cursor.moveToNext()) {
            tn = cursor.getString(1);
        }
        cursor.close();
        return tn;
    }

    public boolean registerTeacher(String name, String username, String password) {
        String teacherExists = getTeacher(username);

        if (teacherExists.equals("")) {
            ContentValues values = new ContentValues();
            values.put(TeacherContract.Teacher.COLUMN_NAME_NAME, name);
            values.put(TeacherContract.Teacher.COLUMN_NAME_USERNAME, username);
            values.put(TeacherContract.Teacher.COLUMN_NAME_PASSWORD, password);
            writableDB.insert(TeacherContract.Teacher.TABLE_NAME, null, values);
            return true;
        } else {
            Toast.makeText(this.context, "Teacher already exists!", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    public JSONArray getLessons(String classID) {
        if (classID == null || classID.isEmpty()) {
            Toast.makeText(this.context, "Something bad happened. Please try again", Toast.LENGTH_LONG).show();
            return null;
        }
        String[] projection = {
                BaseColumns._ID,
                LessonContract.Lesson.COLUMN_NAME_NAME,
                LessonContract.Lesson.COLUMN_NAME_LINK,
                LessonContract.Lesson.COLUMN_NAME_METADATA,
                LessonContract.Lesson.COLUMN_NAME_LOCAL_LINK
        };

        // Filter results from Lesson name
        String selection = LessonContract.Lesson.COLUMN_NAME_CLASSID + " = ?";
        String[] selectionArgs = { classID };


        Cursor cursor = readableDB.query(
                LessonContract.Lesson.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONObject json = null;
        JSONArray lessons = new JSONArray();
        while(cursor.moveToNext()) {
            try {
                json = new JSONObject();
                json.put("name", cursor.getString(1));
                json.put("link", cursor.getString(2));
                json.put("metadata", cursor.getString(3));
                json.put("local_link", cursor.getString(4));
                lessons.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return lessons;
    }
}
