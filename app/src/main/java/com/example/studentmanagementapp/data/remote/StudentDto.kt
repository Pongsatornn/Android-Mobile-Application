package com.example.studentmanagementapp.data.remote

import com.example.studentmanagementapp.data.local.StudentEntity
import com.example.studentmanagementapp.data.local.SyncStatus

// DTO = หน้าตา JSON ที่ FastAPI ส่งมา (ชื่อ field ต้องตรงกับ JSON ของ Backend: student_code)
data class StudentDto(
    val id: Int,
    val student_code: String,
    val name: String,
    val major: String
)

// Body สำหรับ POST / PUT (ไม่มี id เพราะ server เป็นคนสร้าง)
data class StudentCreateRequest(
    val student_code: String,
    val name: String,
    val major: String
)

// ข้อมูลจาก Server = Sync แล้วแน่นอน
fun StudentDto.toEntity(): StudentEntity = StudentEntity(
    serverId = id,
    studentCode = student_code,
    name = name,
    major = major,
    syncStatus = SyncStatus.SYNCED
)

fun StudentEntity.toRequest(): StudentCreateRequest = StudentCreateRequest(
    student_code = studentCode,
    name = name,
    major = major
)
