package com.example.studentmanagementapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 = เครื่อง PC ที่รัน Emulator อยู่ (FastAPI port 8000)
    // ถ้าใช้มือถือจริง ให้เปลี่ยนเป็น IP ของ PC เช่น "http://192.168.1.100:8000/"
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: StudentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StudentApi::class.java)
    }
}
