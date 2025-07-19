package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.*
import kotlinx.coroutines.launch

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

    suspend fun getMoodboardItemById(id: Long): MoodboardItem? {
        return repository.getMoodboardItemById(id)
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