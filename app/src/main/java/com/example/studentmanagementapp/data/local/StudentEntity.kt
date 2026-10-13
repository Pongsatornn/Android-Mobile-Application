package com.example.studentmanagementapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// สถานะการ Sync ของแต่ละแถว
object SyncStatus {
    const val PENDING = "PENDING"   // ยังไม่ส่งไป Server
    const val SYNCED = "SYNCED"     // ส่งไป Server สำเร็จ
    const val FAILED = "FAILED"     // Server ปฏิเสธ ต้องตรวจสอบ/ลองใหม่
}

@Entity(tableName = "students")
data class StudentEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,           // รหัสภายในเครื่อง (Room สร้างให้)

    val serverId: Int? = null,      // id จาก PostgreSQL, null = ยังไม่เคยขึ้น Server

    val studentCode: String,
    val name: String,
    val major: String,

    val syncStatus: String = SyncStatus.PENDING,

    val updatedAt: Long =
        System.currentTimeMillis()
)
