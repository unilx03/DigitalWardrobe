package com.digitalwardrobe.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date

@Entity(tableName = "wearable_table")
@Parcelize
data class Wearable(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @NonNull
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
    val price: String,

    @ColumnInfo(name = "wearable_season")
    val season: String,

    @ColumnInfo(name = "wearable_notes")
    val notes: String?
) : Serializable, Parcelable