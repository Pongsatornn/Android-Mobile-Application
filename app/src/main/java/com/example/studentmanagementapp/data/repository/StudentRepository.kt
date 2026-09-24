package com.example.studentmanagementapp.data.repository

import com.example.studentmanagementapp.data.local.StudentDao
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.toStudent
import com.example.studentmanagementapp.model.Student

// ผลลัพธ์ที่บอกด้วยว่าได้ข้อมูลจาก API สำเร็จไหม (ใช้ทำ Online/Offline)
data class StudentResult(
    val students: List<Student>,
    val isOnline: Boolean
)

class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {

    // Offline-First: ลอง API -> สำเร็จก็บันทึกลง Room -> อ่านจาก Room เสมอ
    suspend fun getStudents(): StudentResult {
        return try {
            val remote = api.getStudents().map { it.toStudent() }
            dao.insertAll(remote)
            StudentResult(dao.getAllStudents(), isOnline = true)
        } catch (e: Exception) {
            // ไม่มีเน็ต / API ล่ม -> ใช้ข้อมูลในเครื่อง
            StudentResult(dao.getAllStudents(), isOnline = false)
        }
    }

    suspend fun getLocalStudents(): List<Student> = dao.getAllStudents()

    suspend fun saveStudent(student: Student) = dao.insertStudent(student)

    suspend fun deleteStudent(student: Student) = dao.deleteStudent(student)
}
