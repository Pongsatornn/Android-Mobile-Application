package com.example.studentmanagementapp.domain.model

// ผลการ Sync รายการ PENDING ขึ้น Server
data class SyncResult(
    val synced: Int,        // ส่งสำเร็จกี่รายการ
    val failed: Int,        // Server ปฏิเสธกี่รายการ
    val isOnline: Boolean   // false = ต่อ Server ไม่ได้ รายการยังเป็น PENDING
)
