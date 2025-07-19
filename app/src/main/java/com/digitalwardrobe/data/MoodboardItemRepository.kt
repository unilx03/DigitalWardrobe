package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData

class MoodboardItemRepository(app: Application, private val dao: MoodboardItemDao) {
    suspend fun getAllMoodboardItems() : List<MoodboardItem> {
        return dao.getAllMoodboardItems()
    }

    suspend fun insert(item: MoodboardItem) : Long {
        return dao.insert(item)
    }

    suspend fun update(item: MoodboardItem) {
        dao.update(item)
    }

    suspend fun delete(item: MoodboardItem) {
        dao.delete(item)
    }

    suspend fun getMoodboardItemById(id: Long): MoodboardItem? {
        return dao.getMoodboardItemById(id)
    }
}