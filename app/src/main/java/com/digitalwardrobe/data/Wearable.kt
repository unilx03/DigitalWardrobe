package com.digitalwardrobe.data

import android.os.Parcel
import android.os.Parcelable

data class Wearable(var dataImage:Int, var dataTitle:String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dataImage)
        parcel.writeString(dataTitle)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Wearable> {
        override fun createFromParcel(parcel: Parcel): Wearable {
            return Wearable(parcel)
        }
        override fun newArray(size: Int): Array<Wearable?> {
            return arrayOfNulls(size)
        }
    }
}