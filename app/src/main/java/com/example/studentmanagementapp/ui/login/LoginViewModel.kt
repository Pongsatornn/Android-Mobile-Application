package com.example.studentmanagementapp.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studentmanagementapp.StudentManagementApplication
import com.example.studentmanagementapp.data.remote.AuthApi
import com.example.studentmanagementapp.data.remote.TokenStorage
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onUsernameChange(value: String) {
        username = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    // POST /token -> ได้ JWT -> เก็บลง TokenStorage -> ไปหน้าหลัก
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val result = authApi.login(
                    username = username.trim(),
                    password = password
                )

                tokenStorage.saveToken(
                    result.access_token
                )

                password = ""
                onSuccess()

            } catch (e: Exception) {
                errorMessage =
                    "เข้าสู่ระบบไม่สำเร็จ กรุณาตรวจสอบข้อมูล"
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as StudentManagementApplication
                LoginViewModel(app.container.authApi, app.container.tokenStorage)
            }
        }
    }
}
