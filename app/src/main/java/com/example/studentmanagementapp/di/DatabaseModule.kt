package com.example.studentmanagementapp.di

import android.content.Context
import androidx.room.Room
import com.example.studentmanagementapp.data.local.AppDatabase
import com.example.studentmanagementapp.data.local.StudentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Lab 6: บอก Hilt ว่าถ้ามี Class ไหนต้องการ AppDatabase หรือ StudentDao ให้สร้างจากฟังก์ชันเหล่านี้
// SingletonComponent = อยู่ตลอดอายุของแอป
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // @Singleton: ทั้งแอปใช้ Database ตัวเดียว (แทน companion getInstance() เดิม)
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "student_db"   // ใช้ชื่อเดิมจาก Lab 4 ข้อมูลในเครื่องจะได้ไม่หาย
        )
            // เปลี่ยนโครงสร้างตาราง -> ล้าง Room แล้วโหลดจาก Server ใหม่ (Room เป็นแค่ cache)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideStudentDao(
        database: AppDatabase
    ): StudentDao {
        return database.studentDao()
    }
}
