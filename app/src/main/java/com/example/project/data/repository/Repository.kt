package com.example.project.data.repository

import com.example.project.data.Entity.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import javax.inject.Inject

class TaskRepository @Inject constructor() {
    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getTasks(onResult: (List<Task>) -> Unit) {
        val userId = getUserId()
        if (userId == null) {
            onResult(emptyList())
            return
        }

        db.child("tasks").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(Task::class.java)?.copy(id = it.key ?: "")
                    }
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun addTask(task: Task, onComplete: () -> Unit, onError: (String) -> Unit = {}) {
        val userId = getUserId()
        if (userId == null) {
            onError("User not authenticated")
            return
        }

        val newRef = db.child("tasks").child(userId).push()
        val newTask = task.copy(id = newRef.key ?: "")

        newRef.setValue(newTask)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> onError(e.message ?: "Unknown error") }
    }


    fun deleteTask(
        taskId: String,
        onComplete: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val userId = getUserId()
        if (userId == null) {
            onError("User not authenticated")
            return
        }

        db.child("tasks").child(userId).child(taskId)
            .removeValue()
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> onError(e.message ?: "Unknown error") }
    }


    fun updateTask(
        task: Task,
        onComplete: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val userId = getUserId()
        if (userId == null) {
            onError("User not authenticated")
            return
        }

        val taskRef = db.child("tasks").child(userId).child(task.id)
        taskRef.setValue(task)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> onError(e.message ?: "Unknown error") }
    }

}
