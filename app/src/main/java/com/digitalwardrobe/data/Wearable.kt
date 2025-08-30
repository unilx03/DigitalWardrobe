package com.digitalwardrobe.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "wearable_table")
@Parcelize
data class Wearable(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "wearable_image")
    var image: String,

    @ColumnInfo(name = "wearable_addDate")
    var addDate: String,

    @ColumnInfo(name = "wearable_category")
    val category: String,

    @ColumnInfo(name = "wearable_colors")
    val colors: String,

    @ColumnInfo(name = "wearable_tags")
    val tags: String,

    @ColumnInfo(name = "wearable_brand")
    val brand: String,

    @ColumnInfo(name = "wearable_price")
    val price: Double,

    @ColumnInfo(name = "wearable_temperature")
    val temperature: String,

    @ColumnInfo(name = "wearable_location_lat")
    val locationLat: Double?,

    @ColumnInfo(name = "wearable_location_lng")
    val locationLng: Double?,

    @ColumnInfo(name = "wearable_notes")
    val notes: String?
) : Serializable, Parcelable