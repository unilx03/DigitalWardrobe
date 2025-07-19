package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyOutfitDao {
    @Query("SELECT * FROM daily_outfit_table")
    suspend fun getAllDailyOutfits(): List<DailyOutfit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dailyOutfit: DailyOutfit) : Long

    @Update
    suspend fun update(dailyOutfit: DailyOutfit)

    @Query("SELECT * FROM daily_outfit_table WHERE id = :dailyOutfitId")
    suspend fun getDailyOutfitById(dailyOutfitId: Long): DailyOutfit?

    @Delete
    suspend fun delete(dailyOutfit: DailyOutfit)
}