package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface OutfitDao {

    @Query("SELECT * FROM outfit_table")
    suspend fun getAllOutfits(): List<Outfit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(outfit: Outfit) : Long

    @Update
    suspend fun update(outfit: Outfit)

    @Delete
    suspend fun delete(outfit: Outfit)

    @Delete
    suspend fun deleteList(outfitList: List<Outfit>)

    @Query("SELECT * FROM outfit_table WHERE id = :id")
    suspend fun getOutfitById(id: Long): Outfit?
}