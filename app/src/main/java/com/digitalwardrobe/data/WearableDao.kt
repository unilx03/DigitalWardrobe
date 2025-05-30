package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WearableDao {

    @Query("SELECT * FROM wearable_table")
    suspend fun getListOfWearables(): LiveData<List<Wearable>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wearable: Wearable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultiple(wearables: List<Wearable>)

    @Delete
    suspend fun deleteList(wearableList: List<Wearable>)

    @Query("DELETE FROM wearable_table WHERE id in (:idList)")
    suspend fun deleteDone(idList: List<String>)

    @Query("SELECT * FROM wearable_table ORDER BY wearable_title")
    fun getWearableByTitle() : LiveData<List<Wearable>> //updates with new content
}