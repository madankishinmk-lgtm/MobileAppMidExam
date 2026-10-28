package com.example.mobileappmidexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentManager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_STUDENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STUDENT_ID + " TEXT UNIQUE, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_AGE + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    public boolean addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, student.getStudentId());
        values.put(COLUMN_NAME, student.getName());
        values.put(COLUMN_AGE, student.getAge());

        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }

    public boolean updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, student.getName());
        values.put(COLUMN_AGE, student.getAge());

        int rows = db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_ID + " = ?",
                new String[]{student.getStudentId()});
        return rows > 0;
    }

    public boolean deleteStudent(String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_STUDENTS, COLUMN_STUDENT_ID + " = ?",
                new String[]{studentId});
        return rows > 0;
    }

    public boolean checkStudentExists(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{COLUMN_ID},
                COLUMN_STUDENT_ID + " = ?",
                new String[]{studentId},
                null, null, null);
        if (cursor != null) {
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        }
        return false;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENTS + " ORDER BY " + COLUMN_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String studentId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE));

                Student student = new Student(id, studentId, name, age);
                studentList.add(student);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return studentList;
    }
}
