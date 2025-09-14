package com.example.to_doapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabase(context: Context) : SQLiteOpenHelper(context, "my_tasks.db", null, 1) {

    companion object {
        private const val TABLE_NAME = "user_tasks"
    }

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "id INTEGER PRIMARY KEY, " +
                "task_text TEXT, " +
                "is_done INTEGER, " +
                "task_category TEXT, " +
                "task_priority TEXT)"
        database.execSQL(createTable)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVer: Int, newVer: Int) {
        database.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(database)
    }

    fun saveAllTasks(taskList: List<Task>) {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME") // clear old data first
        
        for (task in taskList) {
            val values = ContentValues()
            values.put("id", task.id)
            values.put("task_text", task.taskText)
            values.put("is_done", if (task.isDone) 1 else 0)
            values.put("task_category", task.taskCategory)
            values.put("task_priority", task.taskPriority)
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    fun getAllTasks(): MutableList<Task> {
        val taskList = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        
        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(0),
                    taskText = cursor.getString(1),
                    isDone = cursor.getInt(2) == 1,
                    taskCategory = cursor.getString(3),
                    taskPriority = cursor.getString(4)
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return taskList
    }
}