package com.example.studentmanagementapp.domain.model

// Lab 7: Domain Model = ข้อมูลกลางของ Business Logic
// ไม่ใช่ Room Entity และไม่ใช่ API DTO -> ไม่มี import ของ Android / Room / Retrofit เลย
data class Student(
    val id: Int,                    // รหัสภายในเครื่อง (ใช้อ้างอิงแถวเดิมตอน Edit / Delete)
    val studentCode: String,
    val name: String,
    val major: String,
    val serverId: Int? = null,      // null = ยังไม่เคยขึ้น Server
    val syncState: SyncState = SyncState.PENDING
)

enum class SyncState {
    PENDING,    // ยังไม่ส่งไป Server
    SYNCED,     // ส่งไป Server สำเร็จ
    FAILED      // Server ปฏิเสธ ต้องตรวจสอบ/ลองใหม่
}
