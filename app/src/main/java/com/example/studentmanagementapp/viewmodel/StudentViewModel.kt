package com.example.studentmanagementapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studentmanagementapp.StudentManagementApplication
import com.example.studentmanagementapp.data.local.StudentEntity
import com.example.studentmanagementapp.data.local.SyncStatus
import com.example.studentmanagementapp.data.repository.StudentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    // ---------- State ----------

    // Lab 4: รายชื่อมาจาก Room ผ่าน Flow (อัปเดตเองเมื่อ Room เปลี่ยน แม้ออฟไลน์)
    val students: StateFlow<List<StudentEntity>> =
        repository.observeStudents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
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
    fun filter(list: List<StudentEntity>): List<StudentEntity> {
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
                isOnline = repository.refresh()
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
    fun saveStudent(student: StudentEntity?, code: String, name: String, major: String) {
        viewModelScope.launch {
            errorMessage = null
            try {
                if (student == null) {
                    repository.addStudent(code.trim(), name.trim(), major.trim())
                } else {
                    repository.updateStudent(student, code.trim(), name.trim(), major.trim())
                }
            } catch (e: Exception) {
                if (isUnauthorized(e)) return@launch
                errorMessage = "แก้ไขข้อมูลที่ Sync แล้วต้องเชื่อมต่อ Server"
            }
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            errorMessage = null
            try {
                repository.deleteStudent(student)
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
                val result = repository.syncPending()
                isOnline = result.isOnline
                if (result.isOnline) {
                    isOnline = repository.refresh()
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

    fun pendingCount(list: List<StudentEntity>): Int =
        list.count { it.syncStatus != SyncStatus.SYNCED }

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

    // ViewModel มี constructor parameter -> ต้องมี Factory บอกวิธีสร้าง
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as StudentManagementApplication
                StudentViewModel(app.container.studentRepository)
            }
        }
    }
}
