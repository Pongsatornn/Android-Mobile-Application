package com.example.studentmanagementapp.domain.repository

import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow

// Lab 7: บอกว่า Domain ต้องการความสามารถอะไร โดยไม่ระบุว่าใช้ Room หรือ Retrofit อย่างไร
// ตัวที่ทำงานจริงคือ data/repository/StudentRepositoryImpl
interface StudentRepository {

    fun observeStudents(): Flow<List<Student>>

    // ดึงข้อมูลล่าสุดจาก Server: true = Online, false = Offline
    suspend fun refresh(): Boolean

    suspend fun addStudent(student: Student)

    suspend fun updateStudent(student: Student)

    suspend fun deleteStudent(student: Student)

    suspend fun syncPending(): SyncResult
}
