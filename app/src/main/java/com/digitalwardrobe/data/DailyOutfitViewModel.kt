package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DailyOutfitViewModel (application: Application) : ViewModel() {

    private val repository: DailyOutfitRepository

    init {
        val dao = DigitalWardrobeRoomDatabase.getDatabase(application).dailyOutfitDao()
        repository = DailyOutfitRepository(application, dao)
    }

    suspend fun getAllDailyOutfits() : List<DailyOutfit> {
        return repository.getAllDailyOutfits()
    }

    suspend fun insert(outfit: DailyOutfit) : Long {
        return repository.insert(outfit)
    }

    suspend fun update(outfit: DailyOutfit) {
        repository.update(outfit)
    }

    suspend fun delete(outfit: DailyOutfit) {
        repository.delete(outfit)
    }

    suspend fun getDailyOutfitByDate(date: String): DailyOutfit? {
        return repository.getDailyOutfitByDate(date)
    }
}

class DailyOutfitViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyOutfitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyOutfitViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}