package com.fittrack.app.models

import android.os.Parcel
import android.os.Parcelable

data class WorkoutSession(
    val id: Long = 0L,
	val date: String,
	val workout: String,
	val duration: String,
	val volumeKg: String
) : Parcelable {
	constructor(parcel: Parcel) : this(
        parcel.readLong(),
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: ""
	)
	override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
		parcel.writeString(date)
		parcel.writeString(workout)
		parcel.writeString(duration)
		parcel.writeString(volumeKg)
	}
	override fun describeContents() = 0
	companion object CREATOR : Parcelable.Creator<WorkoutSession> {
		override fun createFromParcel(parcel: Parcel) = WorkoutSession(parcel)
		override fun newArray(size: Int) = arrayOfNulls<WorkoutSession>(size)
	}
}