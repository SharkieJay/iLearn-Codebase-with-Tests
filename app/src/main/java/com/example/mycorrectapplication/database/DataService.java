package com.example.mycorrectapplication.database;

import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.COLUMN_NAME_CLASSID;
import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.COLUMN_NAME_LINK;
import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.COLUMN_NAME_LOCAL_LINK;
import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.COLUMN_NAME_METADATA;
import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.COLUMN_NAME_NAME;
import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.COLUMN_NAME_TEACHER;
import static com.example.mycorrectapplication.database.lesson.LessonContract.Lesson.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.example.mycorrectapplication.database.classrooms.ClassroomContract;
import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;
import com.example.mycorrectapplication.database.lesson.LessonContract;
import com.example.mycorrectapplication.database.student.StudentHelper;
import com.example.mycorrectapplication.database.teacher.TeacherHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataService {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase readableDB;
    private SQLiteDatabase writableDB;
    private Context context = null;

    public DataService(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        readableDB = dbHelper.getReadableDatabase();
        writableDB = dbHelper.getWritableDatabase();
    }
    public JSONObject readJSON(String lessonName) {
        if (lessonName == null || lessonName.isEmpty()) {
            Toast.makeText(this.context, "Lesson name cannot be empty!", Toast.LENGTH_LONG).show();
            return null;
        }
        String[] projection = {
                BaseColumns._ID,
                LessonContract.Lesson.COLUMN_NAME_METADATA
        };

        // Filter results from Lesson name
        String selection = LessonContract.Lesson.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { lessonName };


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
        while(cursor.moveToPosition(cursor.getCount()-1)) {
            String item = cursor.getString(1);
            try {
                json = new JSONObject(item);
                break;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return json;
    }

//    public JSONArray getStudentsForClassroom(String classname) {
//        if (classname == null || classname.isEmpty()) {
//            Toast.makeText(this.context, "Something bad happened. Please try again", Toast.LENGTH_LONG).show();
//        }
//        String[] projection = {
//                BaseColumns._ID,
//                ClassroomContract.Classroom.COLUMN_NAME_STUDENTS
//        };
//
//        // Filter results from Lesson name
//        String selection = ClassroomContract.Classroom.COLUMN_NAME_NAME + " = ?";
//        String[] selectionArgs = { classname };
//
//
//        Cursor cursor = readableDB.query(
//                LessonContract.Lesson.TABLE_NAME,   // The table to query
//                projection,             // The array of columns to return (pass null to get all)
//                selection,              // The columns for the WHERE clause
//                selectionArgs,          // The values for the WHERE clause
//                null,                   // don't group the rows
//                null,                   // don't filter by row groups
//                null               // The sort order
//        );
//
//        JSONArray lessons = new JSONArray();
//        while(cursor.moveToNext()) {
//            try {
//                lessons = new JSONArray(cursor.getString(1));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        cursor.close();
//        return lessons;
//    }

    public Boolean addStudentToClassroom(String studentID, String classroom, String classID) {
        StudentHelper studentHelper = new StudentHelper(context);
        ClassroomHelper classroomHelper = new ClassroomHelper(context);

        String student = studentHelper.getStudent(studentID);

        boolean studentAdded = false;

        if(student == null || student.equals("")) {
            Toast.makeText(this.context, "Student does not exist",Toast.LENGTH_LONG).show();
            return studentAdded;
        }

        try {
            studentAdded = studentHelper.addStudentToClassroom(studentID,  classroom, classID);
            if (studentAdded) classroomHelper.updateClassroom(classID, studentID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentAdded;
    }

    public void deleteLesson(String lessonName, String teacherID, String classID) {
        writableDB.execSQL(
                "DELETE FROM " + LessonContract.Lesson.TABLE_NAME + " " +
                "WHERE " + LessonContract.Lesson.COLUMN_NAME_CLASSID + " = '" + classID + "'" +
                "AND " + LessonContract.Lesson.COLUMN_NAME_NAME + " = '" + lessonName + "'" +
                "AND " + LessonContract.Lesson.COLUMN_NAME_TEACHER + " = '" + teacherID + "'"
        );
        Toast.makeText(this.context, "Successfully deleted!", Toast.LENGTH_LONG).show();
    }

    public boolean saveLesson(String name,String metadata,String videoMetadata,String teacherID,String uri,String classID) {
        ContentValues values = new ContentValues();
        TeacherHelper teacherHelper = new TeacherHelper(context);
        JSONArray array = teacherHelper.getLessons(classID);
        boolean lessonExists = false;

        if (array != null) {
            JSONObject obj;
            String objName;
            for (int i = 0; i < array.length(); i++) {
                try {
                    obj = array.getJSONObject(i);
                    objName = obj.get("name").toString();
                    if (name.equals(objName)) {
                        lessonExists = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!lessonExists) {
            values.put(COLUMN_NAME_NAME, name);
            values.put(COLUMN_NAME_METADATA, metadata);
            values.put(COLUMN_NAME_LINK, videoMetadata);
            values.put(COLUMN_NAME_TEACHER, teacherID);
            values.put(COLUMN_NAME_LOCAL_LINK, uri);
            values.put(COLUMN_NAME_CLASSID, classID);
            writableDB.insert(TABLE_NAME, null, values);
            Toast.makeText(this.context, "Successfully saved!", Toast.LENGTH_LONG).show();
            return true;
        }

        Toast.makeText(this.context, "Lesson name already exists!", Toast.LENGTH_LONG).show();
        return false;
    }

    public boolean updateLesson(String name,String metadata,String videoMetadata,String teacherID,String uri,String classID) {
        writableDB.execSQL(
                "UPDATE " + LessonContract.Lesson.TABLE_NAME + " " +
                "SET " + LessonContract.Lesson.COLUMN_NAME_METADATA + " = '" + metadata + "'," +
                LessonContract.Lesson.COLUMN_NAME_LINK + " = '" + videoMetadata + "'," +
                LessonContract.Lesson.COLUMN_NAME_LOCAL_LINK + " = '" + uri + "'" +
                "WHERE " + LessonContract.Lesson.COLUMN_NAME_TEACHER + " = '" + teacherID + "'" +
                "AND " + LessonContract.Lesson.COLUMN_NAME_NAME + " = '" + name + "'" +
                "AND " + LessonContract.Lesson.COLUMN_NAME_CLASSID + " = '" + classID + "'"
        );
        Toast.makeText(this.context, "Successfully updated!", Toast.LENGTH_LONG).show();
        return true;
    }
}
