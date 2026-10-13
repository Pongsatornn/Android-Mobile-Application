package com.example.studentmanagementapp.fake

import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncResult
import com.example.studentmanagementapp.domain.model.SyncState
import com.example.studentmanagementapp.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// Lab 7: Repository ปลอมสำหรับ Unit Test
// เก็บข้อมูลใน Memory ไม่ต้องใช้ Room หรือเรียก API จริง -> ทดสอบเร็วและได้ผลแน่นอนทุกครั้ง
class FakeStudentRepository(
    initial: List<Student> = listOf(
        Student(
            id = 1,
            studentCode = "66001",
            name = "สมชาย",
            major = "IT",
            serverId = 10,
            syncState = SyncState.SYNCED
        )
    )
) : StudentRepository {

    private val students = MutableStateFlow(initial)

    // ตัวแปรไว้ให้ Test ตรวจว่ามีการเรียกฟังก์ชันจริง
    var syncCallCount = 0
        private set
    var online = true
    var syncResult = SyncResult(synced = 0, failed = 0, isOnline = true)

    override fun observeStudents(): Flow<List<Student>> = students

    override suspend fun refresh(): Boolean = online

    override suspend fun addStudent(student: Student) {
        val nextId = (students.value.maxOfOrNull { it.id } ?: 0) + 1
        students.value = students.value + student.copy(id = nextId)
    }

    override suspend fun updateStudent(student: Student) {
        students.value = students.value.map { if (it.id == student.id) student else it }
    }

    override suspend fun deleteStudent(student: Student) {
        students.value = students.value.filterNot { it.id == student.id }
    }

    override suspend fun syncPending(): SyncResult {
        // Fake ไม่ต้องเรียก API จริง
        syncCallCount++
        return syncResult
    }
}
