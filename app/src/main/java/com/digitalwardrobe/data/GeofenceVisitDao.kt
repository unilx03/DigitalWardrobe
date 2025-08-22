package com.digitalwardrobe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GeofenceVisitDao {
    @Insert
    suspend fun insert(visit: GeofenceVisit)

    @Query("SELECT COUNT(*) FROM geofence_visit_table WHERE visit_requestId = :requestId AND visit_timestamp >= :fromTime")
    suspend fun countVisitsSince(requestId: String, fromTime: Long): Int
}