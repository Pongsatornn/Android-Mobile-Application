package com.example.studentmanagementapp.viewmodel

import com.example.studentmanagementapp.domain.model.SyncResult
import com.example.studentmanagementapp.domain.usecase.AddStudentUseCase
import com.example.studentmanagementapp.domain.usecase.DeleteStudentUseCase
import com.example.studentmanagementapp.domain.usecase.GetStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.RefreshStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.SyncStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.UpdateStudentUseCase
import com.example.studentmanagementapp.fake.FakeStudentRepository
import com.example.studentmanagementapp.fake.FakeSyncScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// StudentViewModel แสดง State จาก Use Case (ใช้ Repository / Scheduler ปลอม ไม่ต้องมี Room, API, WorkManager)
@OptIn(ExperimentalCoroutinesApi::class)
class StudentViewModelTest {

    private val repository = FakeStudentRepository()
    private val scheduler = FakeSyncScheduler()

    // viewModelScope ใช้ Dispatchers.Main ซึ่งไม่มีบน JVM -> เปลี่ยนเป็น Test Dispatcher
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = StudentViewModel(
        getStudents = GetStudentsUseCase(repository),
        refreshStudents = RefreshStudentsUseCase(repository),
        addStudent = AddStudentUseCase(repository),
        updateStudent = UpdateStudentUseCase(repository),
        deleteStudentUseCase = DeleteStudentUseCase(repository),
        syncStudentsUseCase = SyncStudentsUseCase(repository),
        syncScheduler = scheduler
    )

    @Test
    fun `students state comes from use case`() = runTest {
        val viewModel = createViewModel()
        // stateIn(WhileSubscribed) จะเริ่มอ่านข้อมูลเมื่อมีผู้สังเกต
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.students.collect() }

        assertEquals(1, viewModel.students.value.size)
        assertEquals("สมชาย", viewModel.students.value.first().name)
    }

    @Test
    fun `adding student updates state and schedules background sync`() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.students.collect() }

        viewModel.saveStudent(null, "66002", "สมหญิง", "CS")

        assertEquals(2, viewModel.students.value.size)
        assertEquals(1, viewModel.pendingCount(viewModel.students.value))
        assertEquals(1, scheduler.syncNowCount)
    }

    @Test
    fun `sync now shows success message`() = runTest {
        repository.syncResult = SyncResult(synced = 3, failed = 0, isOnline = true)
        val viewModel = createViewModel()

        viewModel.syncStudents()

        assertEquals(1, repository.syncCallCount)
        assertEquals("Sync สำเร็จ 3 รายการ", viewModel.syncMessage)
        assertTrue(viewModel.isOnline)
        assertFalse(viewModel.isSyncing)
    }

    @Test
    fun `sync while offline keeps data pending`() = runTest {
        repository.syncResult = SyncResult(synced = 0, failed = 0, isOnline = false)
        val viewModel = createViewModel()

        viewModel.syncStudents()

        assertFalse(viewModel.isOnline)
        assertEquals("ออฟไลน์อยู่ ข้อมูลยังเป็น PENDING ลองใหม่เมื่อมีเน็ต", viewModel.syncMessage)
    }
}
