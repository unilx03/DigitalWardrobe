package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MoodboardItemDao {
    @Query("SELECT * FROM moodboard_item_table")
    suspend fun getAllMoodboardItems(): List<MoodboardItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(moodboardItem: MoodboardItem) : Long

    @Update
    suspend fun update(moodboardItem: MoodboardItem)

    @Query("SELECT * FROM moodboard_item_table WHERE id = :itemId")
    suspend fun getMoodboardItemById(itemId: Long): MoodboardItem?

    @Delete
    suspend fun delete(moodboardItem: MoodboardItem)
}