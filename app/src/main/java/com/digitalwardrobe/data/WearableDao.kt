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
    fun getAllWearables(): LiveData<List<Wearable>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wearable: Wearable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultiple(wearables: List<Wearable>)

    @Update
    suspend fun update(wearable: Wearable)

    @Delete
    suspend fun delete(wearable: Wearable)

    @Delete
    suspend fun deleteList(wearableList: List<Wearable>)

    @Query("DELETE FROM wearable_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM wearable_table WHERE id = :id")
    fun getWearableById(id: Long): LiveData<Wearable>

}