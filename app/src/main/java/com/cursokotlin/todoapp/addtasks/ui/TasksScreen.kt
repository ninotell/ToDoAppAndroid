package com.cursokotlin.todoapp.addtasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.cursokotlin.todoapp.ui.TasksUIState
import com.cursokotlin.todoapp.ui.model.TaskModel

@Composable
fun TasksScreen(tasksViewModel: TasksViewModel) {
    val showNewTaskDialog by tasksViewModel.showNewTaskDialog.observeAsState(initial = false)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<TasksUIState>(
        initialValue = TasksUIState.Loading,
        key1 = lifecycle,
        key2 = tasksViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            tasksViewModel.uiState.collect { value = it }
        }
    }

    when (uiState) {
        is TasksUIState.Error -> {}
        TasksUIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        is TasksUIState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                FAB(Modifier.align(Alignment.BottomEnd)) {
                    tasksViewModel.doOpenAddTaskDialog()
                }
                AddTaskDialog(
                    showNewTaskDialog,
                    tasksViewModel
                )
                Column(Modifier.fillMaxSize()) {
                    Title()
                    TasksList((uiState as TasksUIState.Success).tasks, tasksViewModel)
                }
            }
        }
    }


}

@Composable
fun Title() {
    Text(
        text = "Task list",
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Left,
        color = Color.White,
        modifier = Modifier.padding(18.dp)
    )
}

@Composable
fun TasksList(tasks: List<TaskModel>, tasksViewModel: TasksViewModel) {

    LazyColumn() {
        items(tasks, key = { it.id }) { task ->
            TaskItem(task, tasksViewModel)
        }
    }
}

@Composable
fun TaskItem(taskModel: TaskModel, tasksViewModel: TasksViewModel) {
    var textDecoration: TextDecoration = if (taskModel.selected) {
        TextDecoration.LineThrough
    } else TextDecoration.None

    Row(
        Modifier
            .padding(12.dp, 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .background(Color(0x2BAAA9A9))
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    tasksViewModel.onDeleteTask(taskModel)
                })
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = taskModel.task,
            Modifier.padding(12.dp),
            textDecoration = textDecoration
        )
        Checkbox(checked = taskModel.selected, onCheckedChange = {
            tasksViewModel.onTaskSelected(taskModel)
        })
    }
}

@Composable
fun FAB(modifier: Modifier, onOpenDialog: () -> Unit) {
    FloatingActionButton(modifier = modifier.padding(16.dp),
        onClick = {
            onOpenDialog()
        }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add task fab")
    }
}

@Composable
fun AddTaskDialog(
    show: Boolean,
    tasksViewModel: TasksViewModel
) {
    val newTask by tasksViewModel.newTask.observeAsState(initial = "")

    if (show) {
        Dialog(onDismissRequest = { tasksViewModel.onCancelDialogClick() }) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add a new task",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Divider(
                    Modifier
                        .padding(1.dp)
                        .fillMaxWidth(),
                    color = Color.LightGray
                )
                OutlinedTextField(
                    value = newTask,
                    onValueChange = { tasksViewModel.onNewTaskTextChange(it) },
                    label = { Text(text = "Task description") },
                    modifier = Modifier.padding(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Gray,
                        unfocusedLabelColor = Color.LightGray,
                        textColor = Color.Black,
                        focusedLabelColor = Color(0xff17b987)
                    ),
                    singleLine = true,
                    maxLines = 2
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { tasksViewModel.onCancelDialogClick() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xffc0474b),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = { tasksViewModel.onAddTaskClick(newTask) },
                        enabled = newTask.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xff17b987),
                            contentColor = Color.White,
                            disabledBackgroundColor = Color.LightGray,
                        )
                    ) {
                        Text(text = "Add task")
                    }
                }
            }
        }
    }
}

