package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoodboardItemViewModel(application: Application) : ViewModel() {

    private val repository: MoodboardItemRepository

    init {
        val dao = DigitalWardrobeRoomDatabase.getDatabase(application).moodboardItemDao()
        repository = MoodboardItemRepository(application, dao)
    }

    suspend fun getAllMoodboardItems() : List<MoodboardItem> {
        return repository.getAllMoodboardItems()
    }

    suspend fun insert(item: MoodboardItem): Long {
        return repository.insert(item)
    }

    suspend fun update(item: MoodboardItem) {
        repository.update(item)
    }

    suspend fun delete(item: MoodboardItem) {
        repository.delete(item)
    }
}

class MoodboardItemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodboardItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoodboardItemViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}