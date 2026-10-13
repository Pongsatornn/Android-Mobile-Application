package com.example.studentmanagementapp.data.repository

import com.example.studentmanagementapp.data.local.StudentDao
import com.example.studentmanagementapp.data.local.SyncStatus
import com.example.studentmanagementapp.data.mapper.toDomain
import com.example.studentmanagementapp.data.mapper.toEntity
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.toEntity
import com.example.studentmanagementapp.data.remote.toRequest
import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncResult
import com.example.studentmanagementapp.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Offline-First: Repository เป็นตัวกลางระหว่าง Room (ข้อมูลที่หน้าจออ่าน) และ Retrofit (Server)
// Lab 7: เป็น Implementation ของ domain.repository.StudentRepository
// เชื่อม Domain กับ Room / Retrofit และแปลง Entity <-> Domain Model ที่นี่ที่เดียว
// @Singleton: ViewModel กับ SyncWorker ต้องได้ตัวเดียวกัน syncMutex ถึงจะกันการ Sync ซ้ำได้
@Singleton
class StudentRepositoryImpl @Inject constructor(
    private val dao: StudentDao,
    private val api: StudentApi
) : StudentRepository {

    // Lab 5: ปุ่ม Sync Now กับ SyncWorker อาจทำงานพร้อมกัน
    // ล็อกไว้ให้ Sync ได้ทีละงาน ไม่งั้นรายการ PENDING เดียวกันจะถูกส่งซ้ำ
    private val syncMutex = Mutex()

    // หน้าจออ่านจาก Room เสมอ ไม่ผูกกับการเรียก Server
    override fun observeStudents(): Flow<List<Student>> {
        return dao.observeStudents()
            .map { entities -> entities.map { it.toDomain() } }
    }

    // ดึงข้อมูลล่าสุดจาก Server มาอัปเดต Room
    // return true = Online, false = Offline (หน้าจอยังแสดงข้อมูลเดิมใน Room ได้)
    override suspend fun refresh(): Boolean {
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
    override suspend fun addStudent(student: Student) {
        dao.insertStudent(
            student.toEntity().copy(
                localId = 0,   // ให้ Room สร้าง id ใหม่
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    // 3-6. อ่านรายการ PENDING -> ส่ง API -> เก็บ serverId -> เปลี่ยนเป็น SYNCED
    // ถ้าต่อ Server ไม่ได้จะคืน isOnline = false ให้ผู้เรียก (เช่น SyncWorker) ตัดสินใจ Retry
    override suspend fun syncPending(): SyncResult = syncMutex.withLock {
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
                return@withLock SyncResult(synced, failed, isOnline = false)
            } catch (e: HttpException) {
                if (e.code() == 401) throw e
                // Server ตอบ error (เช่น ข้อมูลไม่ถูกต้อง) -> FAILED
                dao.updateStudent(student.copy(syncStatus = SyncStatus.FAILED))
                failed++
            }
        }

        SyncResult(synced, failed, isOnline = true)
    }

    // Edit (student = ข้อมูลที่แก้แล้ว):
    // - ยังไม่เคยขึ้น Server (serverId = null) -> แก้ใน Room ได้เลย รอ Sync พร้อมกัน
    // - ขึ้น Server แล้ว -> ต้อง PUT ไป Server ก่อน (ต้องออนไลน์)
    override suspend fun updateStudent(student: Student) {
        val edited = student.toEntity()   // updatedAt = เวลาปัจจุบัน

        if (student.serverId == null) {
            dao.updateStudent(edited.copy(syncStatus = SyncStatus.PENDING))
        } else {
            val updated = api.updateStudent(student.serverId, edited.toRequest())
            dao.updateStudent(updated.toEntity().copy(localId = student.id))
        }
    }

    // Delete: ถ้ายังไม่เคยขึ้น Server ลบในเครื่องอย่างเดียว ไม่งั้นต้อง DELETE ที่ Server ก่อน
    override suspend fun deleteStudent(student: Student) {
        if (student.serverId != null) {
            api.deleteStudent(student.serverId)
        }
        dao.deleteStudent(student.toEntity())   // Room ลบตาม Primary Key (localId)
    }
}
