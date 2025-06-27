package com.digitalwardrobe.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "wearable_table")
@Parcelize
data class Wearable(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @NonNull
    @ColumnInfo(name = "wearable_title")
    var title: String,

    @NonNull
    @ColumnInfo(name = "wearable_image")
    var image: String,

    val type: String,

    val color: String,

    val season: String,

    val notes: String?
) : Serializable, Parcelable