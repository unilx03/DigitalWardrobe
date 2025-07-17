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

@Entity(tableName = "outfit_table")
@Parcelize
data class Outfit(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @NonNull
    @ColumnInfo(name = "outfit_preview")
    var preview: String,

    @ColumnInfo(name = "outfit_addDate")
    var addDate: String,

) : Serializable, Parcelable