package com.example.studentmanagementapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // API สำหรับทดลอง — เปลี่ยนเป็น API ของตัวเองได้
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val api: StudentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StudentApi::class.java)
    }
}
