package com.skooldio.android.fundamentals.workshop.pomodoro

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Part 6 : Parcelable
 *
 * แทนที่จะส่งค่าเวลาทีละตัว (3 key) ผ่าน Intent
 * เรายุบให้เป็น "ข้อมูลชุดเดียว" แล้วส่งไปทีเดียว
 *
 * ต้องเปิด plugin ใน build.gradle (Module :app) ก่อน:
 *   plugins {
 *       id 'com.android.application'
 *       id 'kotlin-android'
 *       id 'kotlin-parcelize'   // <-- เพิ่มบรรทัดนี้ แล้วกด Sync Now
 *   }
 *
 * @Parcelize จะ generate โค้ดแปลงเป็น Parcel ให้อัตโนมัติ
 * ไม่ต้องเขียน writeToParcel / createFromParcel เอง
 */
@Parcelize
data class Config(
    val workDuration: Int,
    val shortBreakDuration: Int,
    val longBreakDuration: Int
) : Parcelable
