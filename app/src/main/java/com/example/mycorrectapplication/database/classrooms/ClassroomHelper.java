package com.example.mycorrectapplication.database.classrooms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.example.mycorrectapplication.database.DatabaseHelper;
import com.example.mycorrectapplication.database.lesson.LessonContract;
import com.example.mycorrectapplication.database.student.StudentHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClassroomHelper {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase readableDB;
    private SQLiteDatabase writableDB;
    private Context context = null;

    public ClassroomHelper(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public boolean saveClassroom(String classname, String teacherID) {
        JSONArray classrooms = getClassrooms(teacherID);
        boolean classExists = false;

        if (classrooms != null) {
            JSONObject obj;
            String objName;
            for (int i = 0; i < classrooms.length(); i++) {
                try {
                    obj = classrooms.getJSONObject(i);
                    objName = obj.get("classname").toString();
                    if (classname.equals(objName)) {
                        classExists = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!classExists) {
            ContentValues values = new ContentValues();
            values.put(ClassroomContract.Classroom.COLUMN_NAME_ID, teacherID + "_" + classname);
            values.put(ClassroomContract.Classroom.COLUMN_NAME_NAME, classname);
            values.put(ClassroomContract.Classroom.COLUMN_NAME_TEACHER_ID, teacherID);
            writableDB.insert(ClassroomContract.Classroom.TABLE_NAME, null, values);
            Toast.makeText(this.context, "Successfully created!", Toast.LENGTH_LONG).show();
            return true;
        }
        Toast.makeText(this.context, "Classroom name already exists!", Toast.LENGTH_LONG).show();
        return false;
    }

    public void removeStudent(String studentID, String classID) {
        JSONArray students = getStudents(classID);
        StudentHelper studentHelper = new StudentHelper(context);
        JSONArray classrooms;

        classrooms = studentHelper.getClassrooms(studentID);
        for (int j = 0; j < classrooms.length(); j++) {
            try {
                if (((JSONObject)classrooms.get(j)).getString("classroom_id").equals(classID)) {
                    classrooms.remove(j);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        studentHelper.updateClassrooms(studentID, classrooms);

        if (students != null) {
            for (int i = 0; i < students.length(); i++) {
                try {
                    if (students.getString(i).equals(studentID)) {
                        students.remove(i);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        writableDB.execSQL(
                "UPDATE " + ClassroomContract.Classroom.TABLE_NAME + " " +
                "SET " + ClassroomContract.Classroom.COLUMN_NAME_STUDENTS + " = '" + students + "' " +
                "WHERE " + ClassroomContract.Classroom.COLUMN_NAME_ID + " = '" + classID + "'"
        );
    }

    public void deleteClassroom(String classID) {
        JSONArray students = getStudents(classID);
        StudentHelper studentHelper = new StudentHelper(context);
        JSONArray classrooms;
        String student;

        if (students != null) {
            for (int i = 0; i < students.length(); i++) {
                try {
                    student = students.getString(i);
                    classrooms = studentHelper.getClassrooms(student);
                    for (int j = 0; j < classrooms.length(); j++) {
                        if (((JSONObject)classrooms.get(j)).getString("classroom_id").equals(classID)) {
                            classrooms.remove(j);
                            break;
                        }
                    }
                    studentHelper.updateClassrooms(student, classrooms);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        writableDB.execSQL(
                "DELETE FROM " + ClassroomContract.Classroom.TABLE_NAME + " " +
                "WHERE " + ClassroomContract.Classroom.COLUMN_NAME_ID + " = '" + classID + "'"
        );

        writableDB.execSQL(
                "DELETE FROM " + LessonContract.Lesson.TABLE_NAME + " " +
                "WHERE " + LessonContract.Lesson.COLUMN_NAME_CLASSID + " = '" + classID + "'"
        );
        Toast.makeText(this.context, "Successfully deleted!", Toast.LENGTH_LONG).show();
    }

    public JSONArray getStudents(String classID) {
        String[] projection = {
                BaseColumns._ID,
                ClassroomContract.Classroom.COLUMN_NAME_STUDENTS
        };

        // Filter results from Lesson name
        String selection = ClassroomContract.Classroom.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { classID };

        Cursor cursor = readableDB.query(
                ClassroomContract.Classroom.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray students = null;
        while(cursor.moveToNext()) {
            try {
                if(cursor.getString(1) != null) {
                    students = new JSONArray(cursor.getString(1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return students;
    }

    public void updateClassroom(String classID, String studentID) {
        JSONArray studentsInClass = getStudents(classID);

        if(studentsInClass == null) {
            studentsInClass = new JSONArray();
        }

        studentsInClass.put(studentID);

        writableDB.execSQL(
            "UPDATE " + ClassroomContract.Classroom.TABLE_NAME + " " +
            "SET " + ClassroomContract.Classroom.COLUMN_NAME_STUDENTS + " = '" + studentsInClass + "' " +
            "WHERE " + ClassroomContract.Classroom.COLUMN_NAME_ID + " = '" + classID + "'"
        );
    }

    public JSONArray getClassrooms(String teacherID) {
        String[] projection = {
                BaseColumns._ID,
                ClassroomContract.Classroom.COLUMN_NAME_ID,
                ClassroomContract.Classroom.COLUMN_NAME_NAME
        };

        // Filter results from Lesson name
        String selection = ClassroomContract.Classroom.COLUMN_NAME_TEACHER_ID + " = ?";
        String[] selectionArgs = { teacherID };

        Cursor cursor = readableDB.query(
                ClassroomContract.Classroom.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONArray classrooms = new JSONArray();
        while(cursor.moveToNext()) {
            if(cursor.getString(1) != null) {
                try {
                    JSONObject classroom = new JSONObject();
                    classroom.put("classID", cursor.getString(1));
                    classroom.put("classname", cursor.getString(2));
                    classrooms.put(classroom);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        return classrooms;
    }
}
