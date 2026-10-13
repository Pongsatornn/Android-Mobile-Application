package com.example.studentmanagementapp.data.repository

import com.example.studentmanagementapp.data.local.StudentDao
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.StudentCreateRequest
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

    // Offline-First:
    // Online  -> เรียก API -> ได้ข้อมูล Server -> บันทึก Room -> แสดงผล
    // Offline -> API Error -> อ่าน Room -> แสดงข้อมูลล่าสุดที่มี
    suspend fun getStudents(): StudentResult {
        return try {
            val remote = api.getStudents().map { it.toStudent() }
            dao.replaceAll(remote)
            StudentResult(dao.getAllStudents(), isOnline = true)
        } catch (e: Exception) {
            StudentResult(dao.getAllStudents(), isOnline = false)
        }
    }

    // การเขียนข้อมูลต้องผ่าน Server (PostgreSQL เป็นข้อมูลหลัก)
    // สำเร็จแล้วค่อยอัปเดต Room ให้ตรงกัน ถ้าออฟไลน์จะ throw ให้ ViewModel แจ้ง error

    suspend fun addStudent(code: String, name: String, major: String) {
        val created = api.createStudent(StudentCreateRequest(code, name, major))
        dao.insertStudent(created.toStudent())
    }

    suspend fun updateStudent(id: Int, code: String, name: String, major: String) {
        val updated = api.updateStudent(id, StudentCreateRequest(code, name, major))
        dao.insertStudent(updated.toStudent())
    }

    suspend fun deleteStudent(student: Student) {
        api.deleteStudent(student.id)
        dao.deleteStudent(student)
    }
}
