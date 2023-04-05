package com.cursokotlin.todoapp.addtasks.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Query("SELECT * from TaskEntity")
    fun getTasks(): Flow<List<TaskEntity>>

    @Insert
    suspend fun addTask(item:TaskEntity)

    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)
}