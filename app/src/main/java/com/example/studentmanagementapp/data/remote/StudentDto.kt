package com.example.studentmanagementapp.data.remote

import com.example.studentmanagementapp.model.Student

// DTO = หน้าตา JSON ที่ FastAPI ส่งมา (ชื่อ field ต้องตรงกับ JSON)
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

fun StudentDto.toStudent(): Student = Student(
    id = id,
    studentCode = student_code,
    name = name,
    major = major
)
