package com.example.to_doapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class TaskAdapter(
    private val context: Context,
    private val tasks: MutableList<Task>,
    private val onDeleteClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onCompleteToggle: (Task) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Any = tasks[position]

    override fun getItemId(position: Int): Long = tasks[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_task, parent, false)

        val task = tasks[position]
        val textViewTask = view.findViewById<TextView>(R.id.textViewTask)
        val textViewCategory = view.findViewById<TextView>(R.id.textViewCategory)
        val textViewPriority = view.findViewById<TextView>(R.id.textViewPriority)
        val checkBoxComplete = view.findViewById<CheckBox>(R.id.checkBoxComplete)
        val buttonEdit = view.findViewById<Button>(R.id.buttonEdit)
        val buttonDelete = view.findViewById<Button>(R.id.buttonDelete)

        textViewTask.text = task.taskText
        textViewCategory.text = task.taskCategory
        textViewPriority.text = task.taskPriority
        checkBoxComplete.isChecked = task.isDone

        
        // set category colors
        val categoryColor = when(task.taskCategory) {
            "Work" -> "#FFE0B2"
            "Personal" -> "#E1F5FE" 
            "Shopping" -> "#F3E5F5"
            else -> "#F5F5F5"
        }
        textViewCategory.setBackgroundColor(Color.parseColor(categoryColor))
        
        // priority colors
        when(task.taskPriority) {
            "High" -> textViewPriority.setBackgroundColor(Color.parseColor("#FFCDD2"))
            "Medium" -> textViewPriority.setBackgroundColor(Color.parseColor("#FFF9C4"))
            "Low" -> textViewPriority.setBackgroundColor(Color.parseColor("#C8E6C9"))
            else -> textViewPriority.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        
        // strike through completed tasks
        if (task.isDone) {
            textViewTask.paintFlags = textViewTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textViewTask.paintFlags = textViewTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        checkBoxComplete.setOnCheckedChangeListener { _, checked ->
            task.isDone = checked
            onCompleteToggle(task)
            notifyDataSetChanged()
        }

        buttonEdit.setOnClickListener {
            onEditClick(task)
        }

        buttonDelete.setOnClickListener {
            onDeleteClick(task)
        }

        return view
    }
}
