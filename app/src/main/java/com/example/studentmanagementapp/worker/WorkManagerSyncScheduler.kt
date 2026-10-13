package com.example.studentmanagementapp.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// ผู้สั่งงาน: สร้าง WorkRequest แล้วส่งเข้า WorkManager
// Lab 6: เปลี่ยนจาก object เป็น Class ที่ Hilt สร้างให้ (@Inject constructor) ผู้เรียกไม่ต้องส่ง Context เอง
// Lab 7: เป็น Implementation ของ SyncScheduler (ผูกไว้ใน RepositoryModule)
@Singleton
class WorkManagerSyncScheduler @Inject constructor(
    @ApplicationContext context: Context
) : SyncScheduler {

    private val workManager = WorkManager.getInstance(context)

    // งานจะเริ่มก็ต่อเมื่อมี Network เท่านั้น
    private val networkConstraints =
        Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()

    // One-Time: ทำครั้งเดียว (ถ้าออฟไลน์อยู่ งานจะรอจน Network กลับมา)
    override fun syncNow() {
        val request =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(networkConstraints)
                .build()

        // KEEP: ถ้ามีงานค้างรออยู่แล้วไม่ต้องเพิ่มซ้ำ
        workManager.enqueueUniqueWork(SYNC_NOW, ExistingWorkPolicy.KEEP, request)
    }

    // Periodic: ทำซ้ำทุก 15 นาที (ค่าต่ำสุดที่ Android อนุญาต และระบบอาจเลื่อนเวลาได้)
    override fun schedulePeriodicSync() {
        val request =
            PeriodicWorkRequestBuilder<SyncWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(networkConstraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    // สถานะของงาน Sync in Background ล่าสุด (ไว้แสดงบนหน้าจอ)
    override fun observeSyncNow(): Flow<WorkInfo.State?> =
        workManager
            .getWorkInfosForUniqueWorkFlow(SYNC_NOW)
            .map { it.lastOrNull()?.state }

    private companion object {
        const val SYNC_NOW = "student-sync-now"
        const val PERIODIC_SYNC = "student-sync"
    }
}
