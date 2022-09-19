package com.example.mycorrectapplication.database.lesson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.example.mycorrectapplication.database.DatabaseHelper;
import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LessonHelper {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase readableDB;
    private SQLiteDatabase writableDB;
    private Context context = null;

    public LessonHelper(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }
    public JSONArray exportClassroom (String teacherID, String className) {
        String[] projection = {
                BaseColumns._ID,
                LessonContract.Lesson.COLUMN_NAME_NAME,
                LessonContract.Lesson.COLUMN_NAME_LOCAL_LINK,
                LessonContract.Lesson.COLUMN_NAME_LINK,
                LessonContract.Lesson.COLUMN_NAME_METADATA,
        };

        // Filter results from Lesson name
        String selection = LessonContract.Lesson.COLUMN_NAME_CLASSID + " = ?";
        String[] selectionArgs = { teacherID + "_" + className };


        Cursor cursor = readableDB.query(
                LessonContract.Lesson.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray classroom =  new JSONArray();
        JSONObject json = null;
        while(cursor.moveToNext()) {
            try {
                json = new JSONObject();
                json.put("className", className);
                json.put("lessonName",cursor.getString(1));
                json.put("localLink",cursor.getString(2));
                json.put("link",cursor.getString(3));
                json.put("metadata",cursor.getString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            classroom.put(json);
        }
        cursor.close();
        return classroom;
    }

    public void importClassroom (JSONArray classroom, String teacherID) {
        ContentValues values;
        JSONObject object;
        String classname = "";
        ClassroomHelper classroomHelper = new ClassroomHelper(context);

        for (int i = 0; i < classroom.length(); i++) {
            values = new ContentValues();
            try {
                object = classroom.getJSONObject(i);
                classname = object.getString("className");
                values.put(LessonContract.Lesson.COLUMN_NAME_NAME, object.getString("lessonName"));
                values.put(LessonContract.Lesson.COLUMN_NAME_METADATA, object.getString("metadata"));
                values.put(LessonContract.Lesson.COLUMN_NAME_LINK, object.getString("link"));
                values.put(LessonContract.Lesson.COLUMN_NAME_TEACHER, teacherID);
                values.put(LessonContract.Lesson.COLUMN_NAME_LOCAL_LINK, object.getString("localLink"));
                values.put(LessonContract.Lesson.COLUMN_NAME_CLASSID, teacherID + "_" + classname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            writableDB.insert(LessonContract.Lesson.TABLE_NAME, null, values);
            Toast.makeText(context,"Classroom imported successfully!", Toast. LENGTH_SHORT).show();
        }

        classroomHelper.saveClassroom(classname, teacherID);
    }
}