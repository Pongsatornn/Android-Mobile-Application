package com.example.studentmanagementapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studentmanagementapp.model.Student
import com.example.studentmanagementapp.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    onStudentClick: (Student) -> Unit,
    onAddClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Management") },
                actions = {
                    OnlineStatus(isOnline = viewModel.isOnline)
                    TextButton(onClick = viewModel::loadStudents) { Text("Reload") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("🔍 Search student...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            viewModel.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                StudentList(
                    students = viewModel.filteredStudents,
                    onStudentClick = onStudentClick,
                    onDeleteClick = viewModel::deleteStudent
                )
            }
        }
    }
}

@Composable
fun OnlineStatus(isOnline: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("●", color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828))
        Spacer(Modifier.width(4.dp))
        Text(if (isOnline) "Online" else "Offline", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun StudentList(
    students: List<Student>,
    onStudentClick: (Student) -> Unit,
    onDeleteClick: (Student) -> Unit
) {
    if (students.isEmpty()) {
        Text("ไม่พบข้อมูลนักศึกษา")
        return
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 88.dp)   // เว้นที่ให้ปุ่ม +
    ) {
        items(students, key = { it.id }) { student ->
            StudentItem(
                student = student,
                onClick = { onStudentClick(student) },
                onDelete = { onDeleteClick(student) }
            )
        }
    }
}

@Composable
fun StudentItem(student: Student, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👤", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${student.studentCode} ${student.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(student.major, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
