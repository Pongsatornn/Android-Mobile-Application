package com.example.studentmanagementapp.di

import android.content.Context
import com.example.studentmanagementapp.data.remote.AuthApi
import com.example.studentmanagementapp.data.remote.AuthInterceptor
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Lab 6: ย้ายการสร้าง TokenStorage / OkHttp / Retrofit / Api จาก AppContainer + RetrofitClient มาไว้ที่นี่
// Hilt ต่อสายให้เอง: TokenStorage -> OkHttpClient -> Retrofit -> StudentApi / AuthApi
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 10.0.2.2 = เครื่อง PC ที่รัน Emulator อยู่ (FastAPI port 8000)
    // ถ้าใช้มือถือจริง ให้เปลี่ยนเป็น IP ของ PC เช่น "http://192.168.1.100:8000/"
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // ต้องเป็น @Singleton: LoginViewModel เขียน Token, AuthInterceptor อ่าน Token จากตัวเดียวกัน
    @Provides
    @Singleton
    fun provideTokenStorage(
        @ApplicationContext context: Context
    ): TokenStorage {
        return TokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenStorage: TokenStorage
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenStorage)
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    fun provideStudentApi(
        retrofit: Retrofit
    ): StudentApi {
        return retrofit.create(StudentApi::class.java)
    }

    @Provides
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}
