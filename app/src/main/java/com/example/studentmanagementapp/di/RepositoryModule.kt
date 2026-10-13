package com.example.studentmanagementapp.di

import com.example.studentmanagementapp.data.repository.StudentRepositoryImpl
import com.example.studentmanagementapp.domain.repository.StudentRepository
import com.example.studentmanagementapp.worker.SyncScheduler
import com.example.studentmanagementapp.worker.WorkManagerSyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Lab 7: ผูก Interface กับ Implementation
// ใครขอ StudentRepository (Interface ใน Domain) Hilt จะส่ง StudentRepositoryImpl (Data) ให้
// Use Case / ViewModel จึงไม่ต้องรู้จัก Room หรือ Retrofit เลย
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        implementation: StudentRepositoryImpl
    ): StudentRepository

    // แยก SyncScheduler เป็น Interface ด้วย เพื่อให้ Unit Test ของ ViewModel ใช้ตัวปลอมแทน WorkManager ได้
    @Binds
    @Singleton
    abstract fun bindSyncScheduler(
        implementation: WorkManagerSyncScheduler
    ): SyncScheduler
}
