package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WearableDao {

    @Query("SELECT * FROM wearable_table")
    suspend fun getAllWearables(): List<Wearable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wearable: Wearable) : Long

    @Update
    suspend fun update(wearable: Wearable)

    @Delete
    suspend fun delete(wearable: Wearable)

    @Delete
    suspend fun deleteList(wearableList: List<Wearable>)

    @Query("DELETE FROM wearable_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM wearable_table WHERE id = :id")
    suspend fun getWearableById(id: Long): Wearable?
}