package com.example.studentmanagementapp.fake

import androidx.work.WorkInfo
import com.example.studentmanagementapp.worker.SyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// SyncScheduler ปลอม: นับจำนวนครั้งที่ถูกสั่ง แทนการส่งงานเข้า WorkManager จริง
class FakeSyncScheduler : SyncScheduler {

    var syncNowCount = 0
        private set

    override fun syncNow() {
        syncNowCount++
    }

    override fun schedulePeriodicSync() {}

    override fun observeSyncNow(): Flow<WorkInfo.State?> = MutableStateFlow(null)
}
