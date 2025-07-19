package com.digitalwardrobe.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "moodboard_item_table")
@Parcelize
data class MoodboardItem(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @NonNull
    @ColumnInfo(name = "item_image")
    var image: String,

    @NonNull
    @ColumnInfo(name = "item_x")
    var itemX: Float,

    @NonNull
    @ColumnInfo(name = "item_y")
    var itemY: Float,

    @NonNull
    @ColumnInfo(name = "item_scale")
    var itemScale: Float,

    @NonNull
    @ColumnInfo(name = "item_zIndex")
    var itemZIndex: Int,

    ) : Serializable, Parcelable