package com.example.studentmanagementapp.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 = เครื่อง PC ที่รัน Emulator อยู่ (FastAPI port 8000)
    // ถ้าใช้มือถือจริง ให้เปลี่ยนเป็น IP ของ PC เช่น "http://192.168.1.100:8000/"
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // เรียกครั้งเดียวใน AppContainer (ไม่สร้าง Retrofit หลายชุด)
    fun create(
        tokenStorage: TokenStorage
    ): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenStorage)
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }
}
