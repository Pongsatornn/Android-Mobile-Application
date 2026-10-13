package com.example.studentmanagementapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StudentEntity::class],
    version = 2,     // Lab 4: เพิ่ม serverId / syncStatus / updatedAt
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton: ทั้งแอปใช้ database ตัวเดียว
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_db"
                )
                    // เปลี่ยนโครงสร้างตาราง -> ล้าง Room แล้วโหลดจาก Server ใหม่ (Room เป็นแค่ cache)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { INSTANCE = it }
            }
    }
}
