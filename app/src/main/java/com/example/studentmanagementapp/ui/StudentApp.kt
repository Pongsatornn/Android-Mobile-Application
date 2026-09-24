package com.example.studentmanagementapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanagementapp.viewmodel.StudentViewModel

// การสลับหน้าแบบง่าย (ไม่ต้องใช้ Navigation library)
sealed interface Screen {
    data object List : Screen
    data object Add : Screen
    data class Detail(val id: Int) : Screen
    data class Edit(val id: Int) : Screen
}

@Composable
fun StudentApp(
    viewModel: StudentViewModel = viewModel(factory = StudentViewModel.Factory)
) {
    var screen by remember { mutableStateOf<Screen>(Screen.List) }

    BackHandler(enabled = screen != Screen.List) { screen = Screen.List }

    when (val s = screen) {
        Screen.List -> StudentListScreen(
            viewModel = viewModel,
            onStudentClick = { screen = Screen.Detail(it.id) },
            onAddClick = { screen = Screen.Add }
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
            val student = viewModel.getStudent(s.id)
            if (student == null) {
                LaunchedEffect(Unit) { screen = Screen.List }
            } else {
                StudentDetailScreen(
                    student = student,
                    onBack = { screen = Screen.List },
                    onEdit = { screen = Screen.Edit(student.id) },
                    onDelete = {
                        viewModel.deleteStudent(student)
                        screen = Screen.List
                    }
                )
            }
        }

        is Screen.Edit -> {
            val student = viewModel.getStudent(s.id)
            if (student == null) {
                LaunchedEffect(Unit) { screen = Screen.List }
            } else {
                AddEditStudentScreen(
                    student = student,
                    onSave = { code, name, major ->
                        viewModel.saveStudent(student.id, code, name, major)
                        screen = Screen.Detail(student.id)
                    },
                    onBack = { screen = Screen.Detail(student.id) }
                )
            }
        }
    }
}
