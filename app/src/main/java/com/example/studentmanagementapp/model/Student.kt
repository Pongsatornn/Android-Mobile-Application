package com.example.studentmanagementapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // 0 = ให้ Room สร้าง id ให้ตอนเพิ่มใหม่
    val studentCode: String,
    val name: String,
    val major: String
)
