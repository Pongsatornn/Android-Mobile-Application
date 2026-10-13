package com.example.studentmanagementapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.model.SyncState
import com.example.studentmanagementapp.domain.usecase.AddStudentUseCase
import com.example.studentmanagementapp.domain.usecase.DeleteStudentUseCase
import com.example.studentmanagementapp.domain.usecase.GetStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.RefreshStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.SyncStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.UpdateStudentUseCase
import com.example.studentmanagementapp.worker.SyncScheduler
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

// Lab 6: @HiltViewModel + @Inject constructor -> Hilt ส่ง Dependency มาให้ ไม่ต้องเขียน Factory เองแล้ว
// Lab 7: รับ Use Case แทน Repository โดยตรง -> ViewModel ไม่รู้จัก Room / Retrofit
@HiltViewModel
class StudentViewModel @Inject constructor(
    private val getStudents: GetStudentsUseCase,
    private val refreshStudents: RefreshStudentsUseCase,
    private val addStudent: AddStudentUseCase,
    private val updateStudent: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase,
    private val syncStudentsUseCase: SyncStudentsUseCase,
    // Lab 5: สั่งงานผ่าน SyncScheduler -> WorkManager -> SyncWorker
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    // ---------- State ----------

    // Lab 4: รายชื่อมาจาก Room ผ่าน Flow (อัปเดตเองเมื่อ Room เปลี่ยน แม้ออฟไลน์)
    // Lab 7: ได้เป็น Domain Model (Student) ผ่าน GetStudentsUseCase
    val students: StateFlow<List<Student>> =
        getStudents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // สถานะงาน Sync in Background (ENQUEUED = รอ Network, RUNNING = กำลังทำ)
    val backgroundSync: StateFlow<WorkInfo.State?> =
        syncScheduler.observeSyncNow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    var isLoading by mutableStateOf(false)
        private set

    var isSyncing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var syncMessage by mutableStateOf<String?>(null)
        private set

    var isOnline by mutableStateOf(false)      // Exercise 4
        private set

    var searchQuery by mutableStateOf("")      // Exercise 3
        private set

    // Lab 3: Server ตอบ 401 -> Token ใช้ไม่ได้แล้ว ต้องกลับไปหน้า Login
    var sessionExpired by mutableStateOf(false)
        private set

    // กรองรายการ (ค้นจาก code / name / major)
    fun filter(list: List<Student>): List<Student> {
        val q = searchQuery.trim()
        if (q.isEmpty()) return list
        return list.filter {
            it.studentCode.contains(q, ignoreCase = true) ||
                it.name.contains(q, ignoreCase = true) ||
                it.major.contains(q, ignoreCase = true)
        }
    }

    // ---------- Events ----------

    // GET /students -> อัปเดต Room (ถ้าออฟไลน์ หน้าจอยังแสดงข้อมูลใน Room ตามเดิม)
    fun loadStudents() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                isOnline = refreshStudents()
            } catch (e: Exception) {
                if (isUnauthorized(e)) return@launch
                errorMessage = "ไม่สามารถโหลดข้อมูลได้"
            } finally {
                isLoading = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    // Add -> ลง Room เป็น PENDING ทันที (ทำได้แม้ออฟไลน์)
    // Edit -> ดูใน Repository ว่าต้องออนไลน์หรือไม่
    fun saveStudent(student: Student?, code: String, name: String, major: String) {
        viewModelScope.launch {
            errorMessage = null
            try {
                if (student == null) {
                    addStudent(code, name, major)
                    // มีรายการ PENDING ใหม่ -> ฝากงานไว้กับ WorkManager
                    // ถ้าออฟไลน์ งานจะรอแล้ว Sync เองเมื่อ Network กลับมา
                    syncScheduler.syncNow()
                } else {
                    updateStudent(student, code, name, major)
                }
            } catch (e: Exception) {
                if (isUnauthorized(e)) return@launch
                errorMessage = "แก้ไขข้อมูลที่ Sync แล้วต้องเชื่อมต่อ Server"
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            errorMessage = null
            try {
                deleteStudentUseCase(student)
            } catch (e: Exception) {
                if (isUnauthorized(e)) return@launch
                errorMessage = "ลบข้อมูลไม่สำเร็จ (เชื่อมต่อ Server ไม่ได้)"
            }
        }
    }

    // Sync Now: ส่ง PENDING ขึ้น Server แล้วดึงข้อมูลล่าสุดกลับมา
    fun syncStudents() {
        viewModelScope.launch {
            isSyncing = true
            errorMessage = null
            syncMessage = null
            try {
                val result = syncStudentsUseCase()
                isOnline = result.isOnline
                if (result.isOnline) {
                    isOnline = refreshStudents()
                }
                syncMessage = when {
                    !result.isOnline -> "ออฟไลน์อยู่ ข้อมูลยังเป็น PENDING ลองใหม่เมื่อมีเน็ต"
                    result.failed > 0 -> "Sync สำเร็จ ${result.synced} รายการ, ล้มเหลว ${result.failed} รายการ"
                    result.synced > 0 -> "Sync สำเร็จ ${result.synced} รายการ"
                    else -> "ไม่มีข้อมูลที่ต้อง Sync"
                }
            } catch (e: Exception) {
                if (isUnauthorized(e)) return@launch
                errorMessage = "Sync ไม่สำเร็จ"
            } finally {
                isSyncing = false
            }
        }
    }

    // Lab 5: ปุ่ม Sync in Background
    fun syncInBackground() {
        syncMessage = null
        syncScheduler.syncNow()
    }

    fun pendingCount(list: List<Student>): Int =
        list.count { it.syncState != SyncState.SYNCED }

    fun onSessionExpiredHandled() {
        sessionExpired = false
    }

    private fun isUnauthorized(e: Exception): Boolean {
        if (e is HttpException && e.code() == 401) {
            sessionExpired = true
            return true
        }
        return false
    }
}
