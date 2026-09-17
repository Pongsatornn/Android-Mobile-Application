package com.example.event_drivenmobileapplication170969

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * State Holder: เก็บข้อมูลทั้งหมดของแอปไว้ที่เดียว
 * ทุกหน้าจอ (Register / Profile / Settings) อ่าน-เขียนจาก object นี้
 * เมื่อค่าเปลี่ยน -> Compose จะ recompose หน้าที่ใช้ค่านั้นให้อัตโนมัติ
 */
class StudentState {
    var name by mutableStateOf("")                 // EditText -> TextField
    var studentId by mutableStateOf("")            // EditText -> TextField
    var degree by mutableStateOf("Bachelor")       // RadioGroup (state เดียว)
    var usePython by mutableStateOf(false)         // CheckBox
    var useKotlin by mutableStateOf(false)         // CheckBox
    var useJava by mutableStateOf(false)           // CheckBox
    var notificationEnabled by mutableStateOf(true) // Switch
    var darkMode by mutableStateOf(false)          // Switch (หน้า Settings)
    var interestLevel by mutableStateOf(50f)       // SeekBar -> Slider
    var isFavorite by mutableStateOf(false)        // ToggleButton
    var registered by mutableStateOf(false)        // Button REGISTER
    var imageUriString by mutableStateOf<String?>(null) // เก็บ Uri ของรูปภาพเป็น String
    var selectedDrawableResId by mutableStateOf<Int?>(null) // เริ่มต้นเป็น null เพื่อให้แสดงไอคอนคนแทน

    // ค่าที่ "คำนวณ" จาก state อื่น (ไม่ต้องเก็บแยก)
    val skills: List<String>
        get() = buildList {
            if (usePython) add("Python")
            if (useKotlin) add("Kotlin")
            if (useJava) add("Java")
        }

    // ProgressBar: App เป็นคนคำนวณเอง ผู้ใช้ลากไม่ได้
    val profileProgress: Float
        get() {
            var done = 0
            if (name.isNotBlank()) done++
            if (studentId.length == 10) done++
            if (skills.isNotEmpty()) done++
            if (interestLevel > 0f) done++
            return done / 4f
        }

    fun reset() {
        name = ""; studentId = ""; degree = "Bachelor"
        usePython = false; useKotlin = false; useJava = false
        interestLevel = 50f; registered = false
        imageUriString = null
        selectedDrawableResId = null
    }
}
