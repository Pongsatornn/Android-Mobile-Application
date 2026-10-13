package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.fake.FakeStudentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetStudentsUseCaseTest {

    @Test
    fun `get students returns student list`() = runTest {
        val repository = FakeStudentRepository()
        val useCase = GetStudentsUseCase(repository)

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("สมชาย", result.first().name)
    }

    @Test
    fun `get students emits new list after student is added`() = runTest {
        val repository = FakeStudentRepository()
        val getStudents = GetStudentsUseCase(repository)
        val addStudent = AddStudentUseCase(repository)

        addStudent("66002", "สมหญิง", "CS")

        val result = getStudents().first()
        assertEquals(2, result.size)
        assertEquals("สมหญิง", result.last().name)
    }
}
