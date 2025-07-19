package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface OutfitWearableDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(outfitWearable: OutfitWearable) : Long

    @Update
    suspend fun update(outfitWearable: OutfitWearable)

    @Query("SELECT * FROM outfit_wearable_table WHERE outfit_id = :outfitId")
    suspend fun getWearablesForOutfit(outfitId: Long): List<OutfitWearable?>

    @Query("SELECT * FROM outfit_wearable_table WHERE outfit_id = :outfitId AND wearable_id = :wearableId")
    suspend fun getWearableForOutfit(outfitId: Long, wearableId: Long): OutfitWearable?

    @Delete
    suspend fun delete(outfitWearable: OutfitWearable)
}