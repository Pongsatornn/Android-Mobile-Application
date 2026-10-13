package com.example.studentmanagementapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanagementapp.viewmodel.StudentViewModel

// การสลับหน้าแบบง่าย (ไม่ต้องใช้ Navigation library)
sealed interface Screen {
    data object List : Screen
    data object Add : Screen
    data class Detail(val id: Int) : Screen   // id = localId ใน Room
    data class Edit(val id: Int) : Screen
}

@Composable
fun StudentApp(
    onLogout: () -> Unit,
    viewModel: StudentViewModel = viewModel(factory = StudentViewModel.Factory)
) {
    var screen by remember { mutableStateOf<Screen>(Screen.List) }

    // Lab 4: สังเกตรายชื่อจาก Room
    val students by viewModel.students.collectAsState()
    fun findStudent(localId: Int) = students.find { it.localId == localId }

    // โหลดทุกครั้งที่เข้าหน้านี้ (รวมถึงหลัง Login ใหม่)
    LaunchedEffect(Unit) { viewModel.loadStudents() }

    // Token หมดอายุ -> กลับไป Login
    LaunchedEffect(viewModel.sessionExpired) {
        if (viewModel.sessionExpired) {
            viewModel.onSessionExpiredHandled()
            onLogout()
        }
    }

    BackHandler(enabled = screen != Screen.List) { screen = Screen.List }

    when (val s = screen) {
        Screen.List -> StudentListScreen(
            viewModel = viewModel,
            students = students,
            onStudentClick = { screen = Screen.Detail(it.localId) },
            onAddClick = { screen = Screen.Add },
            onLogout = onLogout
        )

        Screen.Add -> AddEditStudentScreen(
            student = null,
            onSave = { code, name, major ->
                viewModel.saveStudent(null, code, name, major)
                screen = Screen.List
            },
            onBack = { screen = Screen.List }
        )

        is Screen.Detail -> {
            val student = findStudent(s.id)
            if (student == null) {
                LaunchedEffect(Unit) { screen = Screen.List }
            } else {
                StudentDetailScreen(
                    student = student,
                    onBack = { screen = Screen.List },
                    onEdit = { screen = Screen.Edit(student.localId) },
                    onDelete = {
                        viewModel.deleteStudent(student)
                        screen = Screen.List
                    }
                )
            }
        }

        is Screen.Edit -> {
            val student = findStudent(s.id)
            if (student == null) {
                LaunchedEffect(Unit) { screen = Screen.List }
            } else {
                AddEditStudentScreen(
                    student = student,
                    onSave = { code, name, major ->
                        viewModel.saveStudent(student, code, name, major)
                        screen = Screen.Detail(student.localId)
                    },
                    onBack = { screen = Screen.Detail(student.localId) }
                )
            }
        }
    }
}
