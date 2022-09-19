package com.example.mycorrectapplication.database.student;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.example.mycorrectapplication.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentHelper {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase readableDB;
    private SQLiteDatabase writableDB;
    private Context context = null;

    public StudentHelper(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public String getStudent(String username) {
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_PASSWORD
        };

        // Filter results from Lesson name
        String selection = StudentContract.Student.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
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

    public JSONArray getCompetency (String studentId) {
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_COMPETENCY
        };

        // Filter results from Lesson name
        String selection = StudentContract.Student.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { studentId };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray competency = new JSONArray();

        while(cursor.moveToNext()) {
            try {
                if (cursor.getString(1) != null)
                    competency = new JSONArray(cursor.getString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return competency;
    }

    public JSONArray getCompletedLessons (String studentId) {
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_COMPLETEDLESSONS
        };

        // Filter results from Lesson name
        String selection = StudentContract.Student.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { studentId };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray completedLessons = new JSONArray();

        while(cursor.moveToNext()) {
            try {
                if (cursor.getString(1) != null)
                    completedLessons = new JSONArray(cursor.getString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return completedLessons;
    }

    public String getLearningStyle(String username) {
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_LEARNINGSTYLE
        };

        // Filter results from Lesson name
        String selection = StudentContract.Student.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        String ls = "";
        while(cursor.moveToNext()) {
            ls = cursor.getString(1);
        }
        cursor.close();
        return ls;
    }

    public String getStudentName(String username) {
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_NAME
        };

        // Filter results from Lesson name
        String selection = StudentContract.Student.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        String name = "";
        while(cursor.moveToNext()) {
            name = cursor.getString(1);
        }
        cursor.close();
        return name;
    }

    public boolean registerStudent(String name, String username, String password, String learningStyle) {
        String studentExists = getStudent(username);

        if (studentExists.equals("")) {
            ContentValues values = new ContentValues();
            values.put(StudentContract.Student.COLUMN_NAME_NAME, name);
            values.put(StudentContract.Student.COLUMN_NAME_USERNAME, username);
            values.put(StudentContract.Student.COLUMN_NAME_PASSWORD, password);
            values.put(StudentContract.Student.COLUMN_NAME_LEARNINGSTYLE, learningStyle);
            writableDB.insert(StudentContract.Student.TABLE_NAME, null, values);
            return true;
        } else {
            Toast.makeText(this.context, "Student already exists!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void updateCompetency(String studentID, String classroomId, String lessonId, String competency) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("classroom_id", classroomId);
        json.put("lesson_id", lessonId);
        json.put("competency", competency);

        JSONArray competencyRecord = getCompetency(studentID);

        boolean competencyExists = false;

        if (competencyRecord != null) {
            for (int i = 0; i < competencyRecord.length(); i++) {
                if (competencyRecord.get(i).toString().equals(json.toString())) {
                    competencyExists = true;
                }
            }
        } else {
            competencyRecord = new JSONArray();
        }
        competencyRecord.put(json);

        if (!competencyExists) {
            writableDB.execSQL(
                    "UPDATE " + StudentContract.Student.TABLE_NAME + " " +
                    "SET " + StudentContract.Student.COLUMN_NAME_COMPETENCY + " = '" + competencyRecord.toString() + "' " +
                    "WHERE " + StudentContract.Student.COLUMN_NAME_USERNAME + " = '" + studentID + "'"
            );
        }
    }

    public void updateCompletedLessons(String studentID, String classroomId, String lessonId) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("classroom_id", classroomId);
        json.put("lesson_id", lessonId);

        JSONArray completedLessonsRecord = getCompletedLessons(studentID);

        boolean completedLessonsExists = false;

        if (completedLessonsRecord != null) {
            for (int i = 0; i < completedLessonsRecord.length(); i++) {
                if (completedLessonsRecord.get(i).toString().equals(json.toString())) {
                    completedLessonsExists = true;
                }
            }
        } else {
            completedLessonsRecord = new JSONArray();
        }
        completedLessonsRecord.put(json);

        if (!completedLessonsExists) {
            writableDB.execSQL(
                    "UPDATE " + StudentContract.Student.TABLE_NAME + " " +
                    "SET " + StudentContract.Student.COLUMN_NAME_COMPLETEDLESSONS + " = '" + completedLessonsRecord.toString() + "' " +
                    "WHERE " + StudentContract.Student.COLUMN_NAME_USERNAME + " = '" + studentID + "'"
            );
        }
    }

    public JSONArray getClassrooms(String username) {
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_CLASSROOMS
        };

        // Filter results from Lesson name
        String selection = StudentContract.Student.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray classrooms = null;
        while(cursor.moveToNext()) {
            try {
                if (cursor.getString(1) != null) {
                    classrooms = new JSONArray(cursor.getString(1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return classrooms;
    }

    public void updateClassrooms(String studentID, JSONArray classrooms) {
        writableDB.execSQL(
                "UPDATE " + StudentContract.Student.TABLE_NAME + " " +
                "SET " + StudentContract.Student.COLUMN_NAME_CLASSROOMS + " = '" + classrooms.toString() + "' " +
                "WHERE " + StudentContract.Student.COLUMN_NAME_USERNAME + " = '" + studentID + "'"
        );
    }

    public boolean addStudentToClassroom(String username, String classroom, String classroomID) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("classroom", classroom);
        json.put("classroom_id", classroomID);

        JSONArray studentClassrooms = getClassrooms(username);

        boolean classroomExists = false;

        if (studentClassrooms != null) {
            for (int i = 0; i < studentClassrooms.length(); i++) {
                if (studentClassrooms.get(i).toString().equals(json.toString())) {
                    classroomExists = true;
                    Toast.makeText(this.context, "Student already exists", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            studentClassrooms = new JSONArray();
        }
        studentClassrooms.put(json);

        if(!classroomExists) {
            writableDB.execSQL(
                "UPDATE " + StudentContract.Student.TABLE_NAME + " " +
                "SET " + StudentContract.Student.COLUMN_NAME_CLASSROOMS + " = '" + studentClassrooms + "' " +
                "WHERE " + StudentContract.Student.COLUMN_NAME_USERNAME + " = '" + username + "'"
            );
            Toast.makeText(this.context, "Student added!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void deleteStudentCompetency(String classroomId, String lessonId, String type) {
        //Required columns
        String[] projection = {
                BaseColumns._ID,
                StudentContract.Student.COLUMN_NAME_USERNAME,
                StudentContract.Student.COLUMN_NAME_COMPETENCY,
                StudentContract.Student.COLUMN_NAME_COMPLETEDLESSONS
        };

        Cursor cursor = readableDB.query(
                StudentContract.Student.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray competencyRecord = null;
        JSONArray completedLessons = null;
        while(cursor.moveToNext()) {
            //columnIndex 1 = username
            if (cursor.getString(1) != null) {
                try {
                    competencyRecord = new JSONArray(cursor.getString(2));
                    completedLessons = new JSONArray(cursor.getString(3));
                    competencyRecord = deleteCompetency(competencyRecord, classroomId, lessonId, type);
                    completedLessons = deleteCompletedLessons(completedLessons, classroomId, lessonId, type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                writableDB.execSQL(
                        "UPDATE " + StudentContract.Student.TABLE_NAME + " " +
                        "SET " + StudentContract.Student.COLUMN_NAME_COMPETENCY + " = '" + competencyRecord.toString() + "' " +
                        ", " + StudentContract.Student.COLUMN_NAME_COMPLETEDLESSONS + " = '" + completedLessons.toString() + "' " +
                        "WHERE " + StudentContract.Student.COLUMN_NAME_USERNAME + " = '" + cursor.getString(1) + "'"
                );
            }
        }
        cursor.close();
    }

    public JSONArray deleteCompetency(JSONArray competencyRecord, String classroomId, String lessonId, String type) throws JSONException {
        for (int i = 0; i < competencyRecord.length(); i++) {
            if (type.equals("lesson") &&
                    (competencyRecord.getJSONObject(i).getString("classroom_id").equals(classroomId) &&
                    competencyRecord.getJSONObject(i).getString("lesson_id").equals(lessonId))){
                competencyRecord.remove(i);
                i--;
            } else if (type.equals("classroom") &&
                    competencyRecord.getJSONObject(i).getString("classroom_id").equals(classroomId)) {
                competencyRecord.remove(i);
                i--;
            }
        }
        return competencyRecord;
    }

    public JSONArray deleteCompletedLessons(JSONArray completedLessons, String classroomId, String lessonId, String type) throws JSONException {
        for (int i = 0; i < completedLessons.length(); i++) {
            if (type.equals("lesson") &&
                    (completedLessons.getJSONObject(i).getString("classroom_id").equals(classroomId) &&
                            completedLessons.getJSONObject(i).getString("lesson_id").equals(lessonId))){
                completedLessons.remove(i);
                i--;
            } else if (type.equals("classroom") &&
                    completedLessons.getJSONObject(i).getString("classroom_id").equals(classroomId)) {
                completedLessons.remove(i);
                i--;
            }
        }
        return completedLessons;
    }
}