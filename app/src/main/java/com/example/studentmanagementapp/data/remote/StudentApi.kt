package com.example.studentmanagementapp.data.remote

import com.example.studentmanagementapp.model.Student
import retrofit2.http.GET

interface StudentApi {
    @GET("users")
    suspend fun getStudents(): List<StudentDto>
}

// DTO = หน้าตาข้อมูลที่ API ส่งมาจริง (jsonplaceholder /users)
// JSON ไม่มี studentCode/major เลยต้องแปลงเองก่อนเก็บลง Room
data class StudentDto(
    val id: Int?,
    val name: String?,
    val company: CompanyDto?
)

data class CompanyDto(val name: String?)

fun StudentDto.toStudent(): Student = Student(
    id = id ?: 0,
    studentCode = "6501%04d".format(id ?: 0),
    name = name ?: "-",
    major = company?.name ?: "-"
)
