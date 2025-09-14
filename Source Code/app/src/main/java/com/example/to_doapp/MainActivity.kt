package com.example.to_doapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var taskInput: EditText
    private lateinit var addBtn: Button
    private lateinit var themeToggleBtn: Button
    private lateinit var tasksList: ListView
    private lateinit var categorySpinner: Spinner
    private lateinit var prioritySpinner: Spinner
    private lateinit var adapter: TaskAdapter
    private val myTasks = mutableListOf<Task>()
    private var taskIdCounter = 1
    private lateinit var prefs: SharedPreferences
    private lateinit var db: TaskDatabase
    private var darkModeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load theme before setting content view
        prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        darkModeEnabled = prefs.getBoolean("dark_mode", false)
        
        if (darkModeEnabled) {
            setTheme(R.style.Theme_ToDoApp_Dark)
        } else {
            setTheme(R.style.Theme_ToDoApp)
        }
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupDatabase()
        setupAdapter()
        setupClickListeners()
        updateThemeButton()
    }

    private fun initViews() {
        taskInput = findViewById(R.id.editTextTask)
        addBtn = findViewById(R.id.buttonAdd)
        themeToggleBtn = findViewById(R.id.buttonToggleTheme)
        tasksList = findViewById(R.id.listViewTasks)
        categorySpinner = findViewById(R.id.spinnerCategory)
        prioritySpinner = findViewById(R.id.spinnerPriority)
        setupSpinners()
    }

    private fun setupAdapter() {
        adapter = TaskAdapter(this, myTasks, 
            onDeleteClick = { task -> removeTask(task) },
            onEditClick = { task -> editTask(task) },
            onCompleteToggle = { task -> db.saveAllTasks(myTasks) }
        )
        tasksList.adapter = adapter
    }

    private fun setupClickListeners() {
        addBtn.setOnClickListener {
            createNewTask()
        }
        
        themeToggleBtn.setOnClickListener {
            switchTheme()
        }
    }

    private fun setupDatabase() {
        db = TaskDatabase(this)
        val savedTasks = db.getAllTasks()
        myTasks.addAll(savedTasks)
        taskIdCounter = (myTasks.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun setupSpinners() {
        val categoryOptions = arrayOf("Personal", "Work", "Shopping")
        val priorityOptions = arrayOf("Low", "Medium", "High")
        
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryOptions)
        prioritySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityOptions)
    }

    private fun createNewTask() {
        val userInput = taskInput.text.toString().trim()
        if (userInput.isNotEmpty()) {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val selectedPriority = prioritySpinner.selectedItem.toString()
            val task = Task(taskIdCounter++, userInput, false, selectedCategory, selectedPriority)
            myTasks.add(task)
            adapter.notifyDataSetChanged()
            taskInput.text.clear()
            db.saveAllTasks(myTasks)
        } else {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeTask(task: Task) {
        myTasks.remove(task)
        adapter.notifyDataSetChanged()
        db.saveAllTasks(myTasks)
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
    }

    private fun editTask(task: Task) {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        val editInput = EditText(this)
        editInput.setText(task.taskText)
        
        dialogBuilder.setTitle("Edit Task")
            .setView(editInput)
            .setPositiveButton("Save") { _, _ ->
                val updatedText = editInput.text.toString().trim()
                if (updatedText.isNotEmpty()) {
                    task.taskText = updatedText
                    adapter.notifyDataSetChanged()
                    db.saveAllTasks(myTasks)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun switchTheme() {
        darkModeEnabled = !darkModeEnabled
        
        prefs.edit()
            .putBoolean("dark_mode", darkModeEnabled)
            .apply()
        
        if (darkModeEnabled) {
            setTheme(R.style.Theme_ToDoApp_Dark)
        } else {
            setTheme(R.style.Theme_ToDoApp)
        }
        
        updateThemeButton()
        
        finish()
        startActivity(intent)
    }

    private fun updateThemeButton() {
        themeToggleBtn.text = if (darkModeEnabled) "Switch to ☀️ Light Mode" else "Switch to 🌙 Dark Mode"
    }
}