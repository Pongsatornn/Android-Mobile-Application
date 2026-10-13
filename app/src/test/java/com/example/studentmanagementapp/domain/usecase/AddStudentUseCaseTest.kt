package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.model.SyncState
import com.example.studentmanagementapp.fake.FakeStudentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AddStudentUseCaseTest {

    @Test
    fun `add student trims input and saves as pending`() = runTest {
        val repository = FakeStudentRepository(initial = emptyList())
        val useCase = AddStudentUseCase(repository)

        useCase("  66003 ", " มานี ", " SE  ")

        val saved = repository.observeStudents().first().single()
        assertEquals("66003", saved.studentCode)
        assertEquals("มานี", saved.name)
        assertEquals("SE", saved.major)
        assertEquals(SyncState.PENDING, saved.syncState)
        assertNull(saved.serverId)
    }
}
