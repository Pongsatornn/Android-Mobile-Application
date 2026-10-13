package com.example.studentmanagementapp.data.repository

import com.example.studentmanagementapp.data.local.StudentDao
import com.example.studentmanagementapp.data.local.StudentEntity
import com.example.studentmanagementapp.data.local.SyncStatus
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.toEntity
import com.example.studentmanagementapp.data.remote.toRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

// ผลการกด Sync Now
data class SyncResult(
    val synced: Int,        // ส่งสำเร็จกี่รายการ
    val failed: Int,        // Server ปฏิเสธกี่รายการ
    val isOnline: Boolean   // false = ต่อ Server ไม่ได้ รายการยังเป็น PENDING
)

// Offline-First: Repository เป็นตัวกลางระหว่าง Room (ข้อมูลที่หน้าจออ่าน) และ Retrofit (Server)
class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {

    // หน้าจออ่านจาก Room เสมอ ไม่ผูกกับการเรียก Server
    fun observeStudents(): Flow<List<StudentEntity>> {
        return dao.observeStudents()
    }

    // ดึงข้อมูลล่าสุดจาก Server มาอัปเดต Room
    // return true = Online, false = Offline (หน้าจอยังแสดงข้อมูลเดิมใน Room ได้)
    suspend fun refresh(): Boolean {
        return try {
            dao.mergeFromServer(api.getStudents().map { it.toEntity() })
            true
        } catch (e: HttpException) {
            // 401 = Token หมดอายุ/ไม่ถูกต้อง ต้อง Login ใหม่ ไม่ใช่ออฟไลน์
            if (e.code() == 401) throw e
            false
        } catch (e: IOException) {
            false
        }
    }

    // 1-2. ผู้ใช้เพิ่มนักศึกษา -> บันทึกลง Room เป็น PENDING ทันที (ไม่ต้องมีเน็ต)
    suspend fun addStudent(code: String, name: String, major: String) {
        dao.insertStudent(
            StudentEntity(
                studentCode = code,
                name = name,
                major = major,
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    // 3-6. อ่านรายการ PENDING -> ส่ง API -> เก็บ serverId -> เปลี่ยนเป็น SYNCED
    suspend fun syncPending(): SyncResult {
        var synced = 0
        var failed = 0

        for (student in dao.getPendingStudents()) {
            try {
                val response = api.createStudent(student.toRequest())
                dao.updateStudent(
                    student.copy(
                        serverId = response.id,
                        syncStatus = SyncStatus.SYNCED,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                synced++
            } catch (e: IOException) {
                // ต่อ Server ไม่ได้ -> หยุด รายการที่เหลือยังเป็น PENDING ไว้ส่งรอบหน้า
                return SyncResult(synced, failed, isOnline = false)
            } catch (e: HttpException) {
                if (e.code() == 401) throw e
                // Server ตอบ error (เช่น ข้อมูลไม่ถูกต้อง) -> FAILED
                dao.updateStudent(student.copy(syncStatus = SyncStatus.FAILED))
                failed++
            }
        }

        return SyncResult(synced, failed, isOnline = true)
    }

    // Edit:
    // - ยังไม่เคยขึ้น Server (serverId = null) -> แก้ใน Room ได้เลย รอ Sync พร้อมกัน
    // - ขึ้น Server แล้ว -> ต้อง PUT ไป Server ก่อน (ต้องออนไลน์)
    suspend fun updateStudent(student: StudentEntity, code: String, name: String, major: String) {
        val edited = student.copy(
            studentCode = code,
            name = name,
            major = major,
            updatedAt = System.currentTimeMillis()
        )

        if (student.serverId == null) {
            dao.updateStudent(edited.copy(syncStatus = SyncStatus.PENDING))
        } else {
            val updated = api.updateStudent(student.serverId, edited.toRequest())
            dao.updateStudent(updated.toEntity().copy(localId = student.localId))
        }
    }

    // Delete: ถ้ายังไม่เคยขึ้น Server ลบในเครื่องอย่างเดียว ไม่งั้นต้อง DELETE ที่ Server ก่อน
    suspend fun deleteStudent(student: StudentEntity) {
        if (student.serverId != null) {
            api.deleteStudent(student.serverId)
        }
        dao.deleteStudent(student)
    }
}
