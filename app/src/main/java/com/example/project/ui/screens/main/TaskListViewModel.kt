package com.example.project.ui.screens.main

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.Entity.Task
import com.example.project.data.Entity.TaskOperationState
import com.example.project.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = mutableStateOf<List<Task>>(emptyList())
    val tasks: State<List<Task>> = _tasks

    private val _filterStatus = mutableStateOf("All")
    val filterStatus: State<String> = _filterStatus

    private val _operationState = mutableStateOf<TaskOperationState>(TaskOperationState.Idle)
    val operationState: State<TaskOperationState> = _operationState


    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _operationState.value = TaskOperationState.Loading
            repository.getTasks { taskList ->
                _operationState.value = TaskOperationState.Success
                _tasks.value = taskList
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            _operationState.value = TaskOperationState.Loading
            repository.addTask(task,
                onComplete = {
                    _operationState.value = TaskOperationState.Success
                    loadTasks()
                },
                onError = { msg ->
                    _operationState.value = TaskOperationState.Error(msg)
                }
            )
        }
    }

    fun updateTask(task: Task) {
        _operationState.value = TaskOperationState.Loading
        repository.updateTask(
            task,
            onComplete = {
                _operationState.value = TaskOperationState.Success
                loadTasks()
            },
            onError = { message ->
                _operationState.value = TaskOperationState.Error(message)
            }
        )
    }


    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _operationState.value = TaskOperationState.Loading
            repository.deleteTask(
                taskId,
                onComplete = {
                    _operationState.value = TaskOperationState.Success
                    loadTasks()
                },
                onError = { message ->
                    _operationState.value = TaskOperationState.Error(message)
                }
            )
        }
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun resetOperationState() {
        _operationState.value = TaskOperationState.Idle
    }

    val filteredTasks: List<Task>
        get() = if (_filterStatus.value == "All") {
            _tasks.value
        } else {
            _tasks.value.filter { it.status == _filterStatus.value }
        }
}

