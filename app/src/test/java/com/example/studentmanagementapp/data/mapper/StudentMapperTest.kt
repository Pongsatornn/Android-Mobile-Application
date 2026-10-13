package com.example.studentmanagementapp.data.mapper

import com.example.studentmanagementapp.data.local.StudentEntity
import com.example.studentmanagementapp.data.local.SyncStatus
import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncState
import org.junit.Assert.assertEquals
import org.junit.Test

// Repository Implementation แปลง Entity (Room) เป็น Domain Model ได้ถูกต้อง
class StudentMapperTest {

    @Test
    fun `entity is mapped to domain model`() {
        val entity = StudentEntity(
            localId = 5,
            serverId = 42,
            studentCode = "66001",
            name = "สมชาย",
            major = "IT",
            syncStatus = SyncStatus.SYNCED
        )

        val student = entity.toDomain()

        assertEquals(
            Student(
                id = 5,
                studentCode = "66001",
                name = "สมชาย",
                major = "IT",
                serverId = 42,
                syncState = SyncState.SYNCED
            ),
            student
        )
    }

    @Test
    fun `every sync status string maps to enum and back`() {
        for (status in listOf(SyncStatus.PENDING, SyncStatus.SYNCED, SyncStatus.FAILED)) {
            val entity = StudentEntity(studentCode = "1", name = "a", major = "b", syncStatus = status)

            assertEquals(status, entity.toDomain().toEntity().syncStatus)
        }
    }
}
