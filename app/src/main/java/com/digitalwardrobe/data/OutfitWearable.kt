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
        ForeignKey(entity = Outfit::class, parentColumns = ["id"], childColumns = ["outfitId"]),
        ForeignKey(entity = Wearable::class, parentColumns = ["id"], childColumns = ["wearableId"])
    ]
)
@Parcelize
data class OutfitWearable(

    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "outfit_id")
    var outfitId: Long,

    @ColumnInfo(name = "wearable_id")
    var wearableId: Long,

    @NonNull
    @ColumnInfo(name = "wearable_x")
    var wearableX: Float,

    @NonNull
    @ColumnInfo(name = "wearable_y")
    var wearableY: Float,

    @NonNull
    @ColumnInfo(name = "wearable_scale")
    var wearableScale: Float,

    @NonNull
    @ColumnInfo(name = "wearable_zIndex")
    var wearableZIndex: Int,

) : Serializable, Parcelable