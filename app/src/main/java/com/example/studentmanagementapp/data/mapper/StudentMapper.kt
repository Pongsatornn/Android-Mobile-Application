package com.example.studentmanagementapp.data.mapper

import com.example.studentmanagementapp.data.local.StudentEntity
import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncState

// Lab 7: แปลงระหว่าง Room Entity (Data) กับ Domain Model
// ชื่อสถานะใน Room ("PENDING" / "SYNCED" / "FAILED") ตรงกับชื่อใน enum SyncState
fun StudentEntity.toDomain(): Student = Student(
    id = localId,
    studentCode = studentCode,
    name = name,
    major = major,
    serverId = serverId,
    syncState = SyncState.valueOf(syncStatus)
)

fun Student.toEntity(): StudentEntity = StudentEntity(
    localId = id,
    serverId = serverId,
    studentCode = studentCode,
    name = name,
    major = major,
    syncStatus = syncState.name
)
