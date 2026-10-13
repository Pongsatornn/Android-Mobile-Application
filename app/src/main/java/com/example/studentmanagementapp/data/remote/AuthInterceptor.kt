package com.example.studentmanagementapp.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

// เติม Header "Authorization: Bearer <token>" ให้ทุก Request ถ้ามี Token
class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val originalRequest = chain.request()

        val token = tokenStorage.getToken()

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()
        } else {
            originalRequest
        }

        // Log ไว้ดูใน Logcat (filter: AuthInterceptor) แสดง Token แค่ 10 ตัวแรก ไม่เปิดเผยทั้งหมด
        Log.d(
            "AuthInterceptor",
            "${newRequest.method} ${newRequest.url.encodedPath} -> " +
                if (token != null) "Authorization: Bearer ${token.take(10)}..." else "no Authorization header"
        )

        return chain.proceed(newRequest)
    }
}
