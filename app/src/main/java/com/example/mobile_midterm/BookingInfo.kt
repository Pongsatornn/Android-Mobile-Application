package com.example.mobile_midterm

import android.os.Parcel
import android.os.Parcelable

data class BookingInfo(
    val name: String,
    val date: String,
    val room: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(room)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BookingInfo> {
        override fun createFromParcel(parcel: Parcel): BookingInfo = BookingInfo(parcel)
        override fun newArray(size: Int): Array<BookingInfo?> = arrayOfNulls(size)
    }
}
