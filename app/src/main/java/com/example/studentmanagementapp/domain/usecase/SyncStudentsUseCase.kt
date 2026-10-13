package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.model.SyncResult
import com.example.studentmanagementapp.domain.repository.StudentRepository
import javax.inject.Inject

// ส่งรายการ PENDING ขึ้น Server (ใช้ทั้งปุ่ม Sync Now และ SyncWorker)
class SyncStudentsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(): SyncResult {
        return repository.syncPending()
    }
}
