package com.cursokotlin.todoapp.ui

import com.cursokotlin.todoapp.ui.model.TaskModel

sealed interface TasksUIState {
    object Loading : TasksUIState
    data class Error(val throwable: Throwable) : TasksUIState
    data class Success(val tasks: List<TaskModel>) : TasksUIState
}