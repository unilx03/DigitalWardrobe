package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class OutfitViewModel(application: Application) : ViewModel() {

    private val repository: OutfitRepository

    init {
        val dao = DigitalWardrobeRoomDatabase.getDatabase(application).outfitDao()
        repository = OutfitRepository(application, dao)
    }

    suspend fun getAllOutfits() : List<Outfit> {
        return repository.getAllOutfits()
    }

    suspend fun insert(outfit: Outfit): Long {
        return repository.insert(outfit)
    }

    suspend fun update(outfit: Outfit) {
        repository.update(outfit)
    }

    suspend fun delete(outfit: Outfit) {
        repository.delete(outfit)
    }

    suspend fun getOutfitById(id: Long): Outfit? {
        return repository.getOutfitById(id)
    }
}

class OutfitViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutfitViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}