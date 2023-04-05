package com.cursokotlin.todoapp.addtasks.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.todoapp.addtasks.domain.AddTaskUseCase
import com.cursokotlin.todoapp.addtasks.domain.DeleteTaskUseCase
import com.cursokotlin.todoapp.addtasks.domain.GetTasksUseCase
import com.cursokotlin.todoapp.addtasks.domain.UpdateTaskUseCase
import com.cursokotlin.todoapp.ui.TasksUIState
import com.cursokotlin.todoapp.ui.TasksUIState.*
import com.cursokotlin.todoapp.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    val uiState: StateFlow<TasksUIState> = getTasksUseCase()
        .map(::Success)
        .catch { Error(it) }
        .stateIn( //Convierte un Flow en un StateFlow
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), //Cuanto tarda en detener
            Loading //Estado inicial en Loading
        )

    private var _showNewTaskDialog = MutableLiveData<Boolean>()
    var showNewTaskDialog: LiveData<Boolean> = _showNewTaskDialog

    private var _newTask = MutableLiveData<String>()
    var newTask: LiveData<String> = _newTask

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
        _showNewTaskDialog.value = false
        _newTask.value = ""

        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = task))
        }
    }

    fun onTaskSelected(taskModel: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }
    }

    fun onDeleteTask(taskModel: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(taskModel)
        }
    }
}
