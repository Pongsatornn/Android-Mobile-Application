package com.example.studentmanagementapp.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    // OAuth2 Password Flow ของ FastAPI: ส่ง username / password แบบ Form
    @FormUrlEncoded
    @POST("token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): TokenResponse
}
