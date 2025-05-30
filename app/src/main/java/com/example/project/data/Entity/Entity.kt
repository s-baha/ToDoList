package com.example.project.data.Entity

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "todo",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

sealed class TaskOperationState {
    data object Idle : TaskOperationState()
    data object Loading : TaskOperationState()
    data object Success : TaskOperationState()
    data class Error(val message: String) : TaskOperationState()
}