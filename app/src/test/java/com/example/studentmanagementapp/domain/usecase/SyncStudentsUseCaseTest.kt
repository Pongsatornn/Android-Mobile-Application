package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.model.SyncResult
import com.example.studentmanagementapp.fake.FakeStudentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncStudentsUseCaseTest {

    @Test
    fun `sync calls repository syncPending`() = runTest {
        val repository = FakeStudentRepository()
        val useCase = SyncStudentsUseCase(repository)

        useCase()

        assertEquals(1, repository.syncCallCount)
    }

    @Test
    fun `sync returns result from repository`() = runTest {
        val expected = SyncResult(synced = 2, failed = 1, isOnline = true)
        val repository = FakeStudentRepository().apply { syncResult = expected }
        val useCase = SyncStudentsUseCase(repository)

        val result = useCase()

        assertEquals(expected, result)
    }
}
