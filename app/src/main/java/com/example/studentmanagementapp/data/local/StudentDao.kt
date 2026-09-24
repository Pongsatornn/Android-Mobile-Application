package com.example.studentmanagementapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studentmanagementapp.model.Student

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY studentCode")
    suspend fun getAllStudents(): List<Student>

    // REPLACE: id ซ้ำ = อัปเดตทับ (ใช้ทั้ง Add และ Edit, และกันแอปพังตอน sync จาก API ซ้ำ)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Delete
    suspend fun deleteStudent(student: Student)
}
