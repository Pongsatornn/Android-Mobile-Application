package com.example.studentmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.studentmanagementapp.data.remote.TokenStorage
import com.example.studentmanagementapp.ui.StudentApp
import com.example.studentmanagementapp.ui.login.LoginScreen
import com.example.studentmanagementapp.ui.theme.StudentManagementAppTheme
import com.example.studentmanagementapp.worker.SyncScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Lab 6: @AndroidEntryPoint -> Activity นี้รับ Dependency จาก Hilt ได้ (ทั้ง @Inject field และ hiltViewModel())
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt ใส่ค่าให้ก่อน onCreate() ทำงาน (แทนการอ่านจาก AppContainer)
    @Inject
    lateinit var tokenStorage: TokenStorage

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lab 5: ลงทะเบียน Periodic Sync ทุก 15 นาที (unique work -> เรียกซ้ำได้ไม่เกิดงานซ้ำ)
        syncScheduler.schedulePeriodicSync()

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
                        viewModel = hiltViewModel(),
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }
}
