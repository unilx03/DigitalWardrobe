package com.digitalwardrobe.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(
    tableName = "outfit_wearable_table",
    foreignKeys = [
        ForeignKey(entity = Outfit::class, parentColumns = ["id"], childColumns = ["outfit_id"]),
        ForeignKey(entity = Wearable::class, parentColumns = ["id"], childColumns = ["wearable_id"])
    ]
)
@Parcelize
data class OutfitWearable(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "outfit_id")
    val outfitId: Long,

    @ColumnInfo(name = "wearable_id")
    val wearableId: Long,

    @ColumnInfo(name = "wearable_x")
    var wearableX: Float,

    @ColumnInfo(name = "wearable_y")
    var wearableY: Float,

    @ColumnInfo(name = "wearable_scale")
    var wearableScale: Float,

    @ColumnInfo(name = "wearable_zIndex")
    var wearableZIndex: Int,

) : Serializable, Parcelable