package com.cursokotlin.todoapp.addtasks.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cursokotlin.todoapp.ui.model.TaskModel
import javax.inject.Inject

class TasksViewModel @Inject constructor() : ViewModel() {

    private var _showNewTaskDialog = MutableLiveData<Boolean>()
    var showNewTaskDialog: LiveData<Boolean> = _showNewTaskDialog

    private var _taskList = mutableStateListOf<TaskModel>()
    var taskList:List<TaskModel> = _taskList

    private var _newTask = MutableLiveData<String>()
    var newTask:LiveData<String> = _newTask

    fun onCancelDialogClick() {
        _showNewTaskDialog.value = false
    }

    fun doOpenAddTaskDialog() {
        _showNewTaskDialog.value = true
    }

    fun onNewTaskTextChange(task: String) {
        _newTask.value = task
    }

    fun onAddTaskClick(task: String) {
        _taskList.add(TaskModel(task = task))
        _showNewTaskDialog.value = false
        _newTask.value = ""
    }

    fun onTaskSelected(taskModel: TaskModel) {
        val taskIndex = _taskList.indexOf(taskModel)
        _taskList[taskIndex] = _taskList[taskIndex].let {
            it.copy(selected = !taskModel.selected)
        }
    }

    fun onDeleteTask(taskModel: TaskModel) {
        val task = taskList.find { it.id == taskModel.id }
        _taskList.remove(task)
    }
}
