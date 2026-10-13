package com.example.studentmanagementapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    // หน้าจออ่านจาก Room เป็นหลัก: ข้อมูลเปลี่ยนเมื่อไหร่ Flow จะส่งค่าใหม่ให้ UI เอง
    @Query("SELECT * FROM students ORDER BY name")
    fun observeStudents(): Flow<List<StudentEntity>>

    // รวม FAILED ด้วย เพื่อให้กด Sync Now แล้วลองส่งใหม่ได้
    @Query("SELECT * FROM students WHERE syncStatus IN ('PENDING', 'FAILED')")
    suspend fun getPendingStudents(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE serverId = :serverId LIMIT 1")
    suspend fun findByServerId(serverId: Int): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE syncStatus = 'SYNCED' AND serverId NOT IN (:serverIds)")
    suspend fun deleteSyncedNotIn(serverIds: List<Int>)

    // รับข้อมูลชุดล่าสุดจาก Server มาอัปเดต Room
    // - แถวที่ยัง PENDING / FAILED ไม่แตะ (ยังไม่ได้ขึ้น Server ห้ามหาย)
    // - แถวที่มีอยู่แล้วใช้ localId เดิม หน้าจอที่เปิดค้างไว้จะไม่หลุด
    // - แถวที่ถูกลบบน Server ก็ลบในเครื่องด้วย
    @Transaction
    suspend fun mergeFromServer(remote: List<StudentEntity>) {
        deleteSyncedNotIn(remote.mapNotNull { it.serverId })
        for (student in remote) {
            val existing = findByServerId(student.serverId!!)
            if (existing == null) {
                insertStudent(student)
            } else if (existing.syncStatus == SyncStatus.SYNCED) {
                updateStudent(student.copy(localId = existing.localId))
            }
        }
    }
}
