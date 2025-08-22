package com.digitalwardrobe.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date

@Entity(
    tableName = "daily_outfit_table",
    indices = [Index(value = ["daily_outfit_date"], unique = true)],
    foreignKeys = [
        ForeignKey(entity = Outfit::class, parentColumns = ["id"], childColumns = ["outfit_id"]),
    ]
)
@Parcelize
data class DailyOutfit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "outfit_id")
    val outfitId: Long,

    @ColumnInfo(name = "daily_outfit_date")
    var date: String,

) : Serializable, Parcelable