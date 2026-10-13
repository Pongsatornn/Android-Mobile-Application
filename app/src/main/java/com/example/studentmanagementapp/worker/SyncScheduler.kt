package com.example.studentmanagementapp.worker

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

// Lab 7: สั่ง Sync เบื้องหลัง (ตัวจริงคือ WorkManagerSyncScheduler)
// เป็น Interface เพื่อให้ Unit Test ของ ViewModel ใช้ตัวปลอมได้โดยไม่ต้องมี WorkManager
interface SyncScheduler {

    // One-Time: ทำครั้งเดียว (ถ้าออฟไลน์อยู่ งานจะรอจน Network กลับมา)
    fun syncNow()

    // Periodic: ทำซ้ำทุก 15 นาที
    fun schedulePeriodicSync()

    // สถานะของงาน Sync in Background ล่าสุด (ไว้แสดงบนหน้าจอ)
    fun observeSyncNow(): Flow<WorkInfo.State?>
}
