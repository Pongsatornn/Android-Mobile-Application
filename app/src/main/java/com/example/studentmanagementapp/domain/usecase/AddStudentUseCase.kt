package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncState
import com.example.studentmanagementapp.domain.repository.StudentRepository
import javax.inject.Inject

// เพิ่มนักศึกษา -> บันทึกในเครื่องเป็น PENDING ก่อน รอ Sync
class AddStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(code: String, name: String, major: String) {
        repository.addStudent(
            Student(
                id = 0,     // ยังไม่มี ให้ฐานข้อมูลสร้างให้
                studentCode = code.trim(),
                name = name.trim(),
                major = major.trim(),
                syncState = SyncState.PENDING
            )
        )
    }
}
