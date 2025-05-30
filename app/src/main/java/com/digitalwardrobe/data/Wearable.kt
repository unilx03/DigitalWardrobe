package com.digitalwardrobe.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wearable_table")
data class Wearable(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @NonNull
    @ColumnInfo(name = "wearable_title")
    var title: String,

    @NonNull
    @ColumnInfo(name = "wearable_image")
    var image: String,
)