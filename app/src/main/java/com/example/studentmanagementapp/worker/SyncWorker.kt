package com.example.studentmanagementapp.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentmanagementapp.domain.usecase.SyncStudentsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

// งานที่ WorkManager สั่งให้ทำเบื้องหลัง: ส่งรายการ PENDING ขึ้น Server
// Lab 6: @HiltWorker -> Hilt ส่ง Dependency มาให้ทาง constructor (ไม่ต้องอ่านจาก AppContainer แล้ว)
// @Assisted = ค่าที่ WorkManager ส่งมาตอนสร้างงาน ส่วน syncStudents มาจาก Hilt
// Lab 7: เรียกผ่าน Use Case แทน Repository โดยตรง (Logic เดียวกับปุ่ม Sync Now)
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncStudents: SyncStudentsUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork() start (attempt ${runAttemptCount + 1})")

        return try {
            val result = syncStudents()

            if (!result.isOnline) {
                // ต่อ Server ไม่ได้ -> ให้ WorkManager ลองใหม่ภายหลัง
                Log.d(TAG, "Server unreachable, synced=${result.synced} -> retry")
                Result.retry()
            } else {
                Log.d(TAG, "Sync done: synced=${result.synced}, failed=${result.failed} -> success")
                Result.success()
            }

        } catch (e: IOException) {
            Log.d(TAG, "Network error -> retry", e)
            Result.retry()
        } catch (e: HttpException) {
            // 401 = Token หมดอายุ ทำเบื้องหลังต่อไม่ได้ ต้องให้ผู้ใช้ Login ใหม่ก่อน
            Log.w(TAG, "HTTP ${e.code()} -> failure")
            Result.failure()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error -> failure", e)
            Result.failure()
        }
    }

    companion object {
        const val TAG = "SyncWorker"
    }
}
