package com.example.krzys.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by krzys on 05.01.2018.
 */

public class taskDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "tasks.db";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String ID_COL_NAME = "_id";
    public static final String TASK_NAME_COL_NAME = "taskName";
    public static final String DATE_COL_NAME = "date";
    public static final String PRIORITY_COL_NAME = "priority";
    public static final String IS_DONE_COL_NAME = "isDone";
    public static final String HAS_ATTACHMENTS_COL_NAME = "hasAttachments";

    public taskDbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXIST "+ TASKS_TABLE_NAME +" ("+
                    ID_COL_NAME+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    TASK_NAME_COL_NAME+" TEXT NOT NULL, "+
                    DATE_COL_NAME+" TEXT NOT NULL, "+
                    PRIORITY_COL_NAME+" INTEGER NOT NULL, "+
                    IS_DONE_COL_NAME+" INTEGER NOT NULL, "+
                    HAS_ATTACHMENTS_COL_NAME+" INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TASKS_TABLE_NAME);
        onCreate(db);
    }

    public boolean addTask(String name, task.priorityLevel priorityLevel, String date, boolean hasAttachments, boolean isDone)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_NAME_COL_NAME, name);
        contentValues.put(DATE_COL_NAME, date);
        contentValues.put(PRIORITY_COL_NAME, priorityLevel.getValue());
        contentValues.put(IS_DONE_COL_NAME, isDone ? 1 : 0);
        contentValues.put(HAS_ATTACHMENTS_COL_NAME, hasAttachments ? 1 : 0);

        long result = db.insert(TASKS_TABLE_NAME, null, contentValues);
        Log.d("insert",Long.toString(result));
        return result == -1 ? false : true;
    }

    public void modifyTask( int id, String name, task.priorityLevel priorityLevel, String date, boolean hasAttachments, boolean isDone)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_NAME_COL_NAME, name);
        contentValues.put(DATE_COL_NAME, date);
        contentValues.put(PRIORITY_COL_NAME, priorityLevel.getValue());
        contentValues.put(IS_DONE_COL_NAME, isDone ? 1 : 0);
        contentValues.put(HAS_ATTACHMENTS_COL_NAME, hasAttachments ? 1 : 0);

        db.update(TASKS_TABLE_NAME, contentValues, ID_COL_NAME + " = ? ", new String[]{Integer.toString(id)});
    }

    public void setTaskDone(int id, boolean isDone)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IS_DONE_COL_NAME, isDone ? 1 : 0);
        db.update(TASKS_TABLE_NAME, contentValues, ID_COL_NAME+" = ? ", new String[]{Integer.toString(id)});
    }

    public void setTaskPriority(int id, int priority)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRIORITY_COL_NAME, priority);
        db.update(TASKS_TABLE_NAME, contentValues, ID_COL_NAME+" = ? ", new String[]{Integer.toString(id)});
    }

    public task getTask( int id)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM "+TASKS_TABLE_NAME+" WHERE "+ID_COL_NAME+" = ? ", new String[]{Integer.toString(id)});

        task.priorityLevel priority = null;

        switch (cur.getInt(3))
        {
            case 0 : priority = task.priorityLevel.low; break;
            case 1 : priority = task.priorityLevel.averange; break;
            case 2 : priority = task.priorityLevel.high; break;
        }
        task todo = new task(cur.getInt(0), cur.getString(1), cur.getString(2), priority , cur.getInt(4) == 1 ? true : false, cur.getInt(5) == 1 ? true : false );
        cur.close();
        return todo;
    }

    public ArrayList<task> getAllTasks()
    {
        ArrayList<task> tasks = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM "+TASKS_TABLE_NAME, null);
        Log.d("Rows result  ", Integer.toString(cur.getCount()));
        Log.d("cursor to firs" , cur.moveToFirst() ? "true" : "false");
        Log.d("is before last", !cur.isAfterLast() ? "true" : "false");
        while (cur.moveToNext())
        {
            task.priorityLevel priority = null;

            switch (cur.getInt(3))
            {
                case 0 : priority = task.priorityLevel.low; break;
                case 1 : priority = task.priorityLevel.averange; break;
                case 2 : priority = task.priorityLevel.high; break;
            }
            task todo = new task(cur.getInt(0), cur.getString(1), cur.getString(2), priority , cur.getInt(4) == 1 ? true : false, cur.getInt(5) == 1 ? true : false );
            Log.d("task ", todo.toString());
            tasks.add(todo);
        }
        cur.close();
        return tasks;
    }

    public int getTasksNumber()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TASKS_TABLE_NAME,null);
        int result;
        if (cur.isFirst())
            result =  cur.getInt(0);
        else
            result = 0;
        cur.close();

        return  result;
    }

    public void deleteTask( int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE_NAME, ID_COL_NAME + " = ?", new String[]{Integer.toString(id)});
    }
}
