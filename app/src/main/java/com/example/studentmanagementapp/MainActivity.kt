package com.example.studentmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanagementapp.ui.StudentApp
import com.example.studentmanagementapp.ui.login.LoginScreen
import com.example.studentmanagementapp.ui.login.LoginViewModel
import com.example.studentmanagementapp.ui.theme.StudentManagementAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenStorage = (application as StudentManagementApplication).container.tokenStorage

        setContent {
            StudentManagementAppTheme {
                // เปิดแอป -> ตรวจ Token: ไม่มี -> LoginScreen, มี -> หน้ารายชื่อนักศึกษา
                var isLoggedIn by remember { mutableStateOf(tokenStorage.getToken() != null) }

                if (isLoggedIn) {
                    StudentApp(
                        onLogout = {
                            tokenStorage.clearToken()
                            isLoggedIn = false
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = viewModel(factory = LoginViewModel.Factory),
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }
}
