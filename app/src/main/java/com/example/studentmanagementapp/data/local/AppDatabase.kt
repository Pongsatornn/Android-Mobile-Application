package com.example.studentmanagementapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Lab 6: ตัวสร้าง Database (เดิมคือ companion getInstance()) ย้ายไปอยู่ที่ di/DatabaseModule.kt
// Hilt ดูแลให้ทั้งแอปใช้ Database ตัวเดียว (@Singleton)
@Database(
    entities = [StudentEntity::class],
    version = 2,     // Lab 4: เพิ่ม serverId / syncStatus / updatedAt
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
}
