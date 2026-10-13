package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.repository.StudentRepository
import javax.inject.Inject

// แก้ไขนักศึกษา (ถ้าขึ้น Server แล้วต้องออนไลน์ ดูใน Repository)
class UpdateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(student: Student, code: String, name: String, major: String) {
        repository.updateStudent(
            student.copy(
                studentCode = code.trim(),
                name = name.trim(),
                major = major.trim()
            )
        )
    }
}
