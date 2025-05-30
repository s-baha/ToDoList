package com.example.project.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project.data.Entity.Task
import com.example.project.data.Entity.TaskOperationState
import com.example.project.ui.components.AddEditTaskDialog
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val tasks by remember { derivedStateOf { viewModel.filteredTasks } }
    val filterStatus by viewModel.filterStatus
    val operationState by viewModel.operationState

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropdownMenuWithFilter(filterStatus, onStatusSelected = { viewModel.setFilterStatus(it) })



                Row {
                    Button(onClick = {
                        taskToEdit = null
                        showAddDialog = true
                    }) {
                        Text("Add Task")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                if (tasks.isEmpty()) {
                    Text(
                        text = "Add some tasks",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn {
                        items(tasks) { task ->
                            TaskCard(
                                task = task,
                                onEdit = {
                                    taskToEdit = task
                                    showAddDialog = true
                                },
                                onDelete = { viewModel.deleteTask(task.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                if (operationState is TaskOperationState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        if (showAddDialog) {
            AddEditTaskDialog(
                onDismissRequest = {
                    showAddDialog = false
                    viewModel.resetOperationState()
                },
                onSaveTask = { task ->
                    if (taskToEdit == null) {
                        viewModel.addTask(task)
                    } else {
                        viewModel.updateTask(task)
                    }
                },
                taskToEdit = taskToEdit
            )
        }
    }
    LaunchedEffect(operationState) {
        when (operationState) {
            is TaskOperationState.Error -> {
                val message = (operationState as TaskOperationState.Error).message
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(message)
                    viewModel.resetOperationState()
                }
            }
            is TaskOperationState.Success -> {
                showAddDialog = false
                viewModel.resetOperationState()
            }
            else -> Unit
        }
    }
}

@Composable
fun TaskCard(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
    val statusColor = when (task.status) {
        "todo" -> Color.Red
        "in_progress" -> Color(0xFFFFC107)
        "done" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(statusColor)
                .align(Alignment.TopEnd)
        )

        Column {
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(task.description, fontSize = 14.sp)
            Text("Status: ${task.status}", color = Color.Gray, fontSize = 12.sp)

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = onEdit) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DropdownMenuWithFilter(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("All", "todo", "in_progress", "done")

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedStatus)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            statuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }


        }
    }
}
