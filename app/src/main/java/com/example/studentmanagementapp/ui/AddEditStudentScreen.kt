package com.example.studentmanagementapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanagementapp.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentScreen(
    student: Student?,                                   // null = Add, มีค่า = Edit
    onSave: (code: String, name: String, major: String) -> Unit,
    onBack: () -> Unit
) {
    // ค่าในฟอร์มเป็น UI state ล้วน ๆ เก็บใน Composable ได้
    var code by rememberSaveable { mutableStateOf(student?.studentCode ?: "") }
    var name by rememberSaveable { mutableStateOf(student?.name ?: "") }
    var major by rememberSaveable { mutableStateOf(student?.major ?: "") }

    val canSave = code.isNotBlank() && name.isNotBlank() && major.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (student == null) "Add Student" else "Edit Student") },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = code, onValueChange = { code = it },
                label = { Text("Student Code") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = major, onValueChange = { major = it },
                label = { Text("Major") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onSave(code, name, major) },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) { Text("SAVE") }
        }
    }
}
