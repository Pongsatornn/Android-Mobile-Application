package com.example.studentmanagementapp

import android.app.Application
import com.example.studentmanagementapp.data.AppContainer

class StudentManagementApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
