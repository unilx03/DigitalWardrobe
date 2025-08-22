package com.digitalwardrobe.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "geofence_visit_table")
@Parcelize
data class GeofenceVisit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "visit_requestId")
    val requestId: String,

    @ColumnInfo(name = "visit_timestamp")
    val timestamp: Long

) : Serializable, Parcelable