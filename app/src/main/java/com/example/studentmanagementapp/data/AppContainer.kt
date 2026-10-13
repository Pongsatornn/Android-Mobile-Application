package com.example.studentmanagementapp.data

import android.content.Context
import com.example.studentmanagementapp.data.local.AppDatabase
import com.example.studentmanagementapp.data.remote.AuthApi
import com.example.studentmanagementapp.data.remote.RetrofitClient
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.TokenStorage
import com.example.studentmanagementapp.data.repository.StudentRepository

// รวม Dependency ทั้งแอปไว้ที่เดียว (สร้างแบบ Manual, Lab 6 จะเปลี่ยนเป็น Hilt)
class AppContainer(context: Context) {

    val tokenStorage = TokenStorage(context)

    private val retrofit = RetrofitClient.create(tokenStorage)

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

    private val studentApi: StudentApi = retrofit.create(StudentApi::class.java)

    val studentRepository = StudentRepository(
        AppDatabase.getInstance(context).studentDao(),
        studentApi
    )
}
