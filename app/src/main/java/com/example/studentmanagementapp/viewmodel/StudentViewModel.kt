package com.example.studentmanagementapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studentmanagementapp.data.local.AppDatabase
import com.example.studentmanagementapp.data.remote.RetrofitClient
import com.example.studentmanagementapp.data.repository.StudentRepository
import com.example.studentmanagementapp.model.Student
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    // ---------- State ----------
    var students by mutableStateOf(listOf<Student>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isOnline by mutableStateOf(false)      // Exercise 4
        private set

    var searchQuery by mutableStateOf("")      // Exercise 3
        private set

    // รายการที่กรองแล้ว (ค้นจาก code / name / major)
    val filteredStudents: List<Student>
        get() {
            val q = searchQuery.trim()
            if (q.isEmpty()) return students
            return students.filter {
                it.studentCode.contains(q, ignoreCase = true) ||
                    it.name.contains(q, ignoreCase = true) ||
                    it.major.contains(q, ignoreCase = true)
            }
        }

    init {
        loadStudents()
    }

    // ---------- Events ----------
    fun loadStudents() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = repository.getStudents()
                students = result.students
                isOnline = result.isOnline
                if (!result.isOnline && result.students.isEmpty()) {
                    errorMessage = "ออฟไลน์อยู่ และยังไม่มีข้อมูลในเครื่อง"
                }
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถโหลดข้อมูลได้"
            } finally {
                isLoading = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    // id = null -> POST /students, มี id -> PUT /students/{id}
    fun saveStudent(id: Int?, code: String, name: String, major: String) {
        viewModelScope.launch {
            errorMessage = null
            try {
                if (id == null) {
                    repository.addStudent(code.trim(), name.trim(), major.trim())
                } else {
                    repository.updateStudent(id, code.trim(), name.trim(), major.trim())
                }
                loadStudents()
            } catch (e: Exception) {
                errorMessage = "บันทึกข้อมูลไม่สำเร็จ (เชื่อมต่อ Server ไม่ได้)"
            }
        }
    }

    // DELETE /students/{id}
    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            errorMessage = null
            try {
                repository.deleteStudent(student)
                loadStudents()
            } catch (e: Exception) {
                errorMessage = "ลบข้อมูลไม่สำเร็จ (เชื่อมต่อ Server ไม่ได้)"
            }
        }
    }

    fun getStudent(id: Int): Student? = students.find { it.id == id }

    // ViewModel มี constructor parameter -> ต้องมี Factory บอกวิธีสร้าง
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val db = AppDatabase.getInstance(app)
                StudentViewModel(
                    StudentRepository(db.studentDao(), RetrofitClient.api)
                )
            }
        }
    }
}
