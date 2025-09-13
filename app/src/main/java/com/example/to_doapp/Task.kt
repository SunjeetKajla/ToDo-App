package com.example.to_doapp

data class Task(
    val id: Int,
    var taskText: String,
    var isDone: Boolean = false,
    var taskCategory: String = "Personal",
    var taskPriority: String = "Medium"
)