package com.example.studentmanagementapp.data.remote

// JSON ที่ POST /token ส่งกลับ (ชื่อ property ต้องตรงกับ JSON)
// { "access_token": "eyJhbGciOi...", "token_type": "bearer" }
data class TokenResponse(
    val access_token: String,
    val token_type: String
)
