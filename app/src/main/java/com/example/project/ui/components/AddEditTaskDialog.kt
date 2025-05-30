package com.example.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project.data.Entity.Task
import com.example.project.data.Entity.TaskOperationState

@Composable
fun AddEditTaskDialog(
    onDismissRequest: () -> Unit,
    onSaveTask: (Task) -> Unit,
    operationState: TaskOperationState = TaskOperationState.Idle,
    taskToEdit: Task? = null,
    onOperationHandled: () -> Unit = {}
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var status by remember { mutableStateOf(taskToEdit?.status ?: "todo") }

    LaunchedEffect(operationState) {
        if (operationState is TaskOperationState.Success) {
            onDismissRequest()
            onOperationHandled()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    val newTask = taskToEdit?.copy(
                        title = title,
                        description = description,
                        status = status,
                        updatedAt = System.currentTimeMillis()
                    ) ?: Task(
                        title = title,
                        description = description,
                        status = status,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    onSaveTask(newTask)
                },
                enabled = operationState != TaskOperationState.Loading
            ) {
                if (operationState == TaskOperationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = operationState != TaskOperationState.Loading
            ) {
                Text("Cancel")
            }
        },
        title = { Text(if (taskToEdit != null) "Edit Task" else "Add Task") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = operationState != TaskOperationState.Loading
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = operationState != TaskOperationState.Loading

                )
                Spacer(modifier = Modifier.height(8.dp))

                StatusDropdown(selectedStatus = status, onStatusSelected = { status = it })
            }
        }
    )
    if (operationState is TaskOperationState.Error) {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = operationState.message)
        }
    }
}

@Composable
fun StatusDropdown(selectedStatus: String, onStatusSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("todo", "in_progress", "done")

    Box {
        OutlinedTextField(
            value = selectedStatus,
            onValueChange = {},
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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
