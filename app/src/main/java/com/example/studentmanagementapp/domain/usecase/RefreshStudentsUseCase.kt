package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.repository.StudentRepository
import javax.inject.Inject

// ดึงข้อมูลล่าสุดจาก Server: true = Online, false = Offline
class RefreshStudentsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.refresh()
    }
}
