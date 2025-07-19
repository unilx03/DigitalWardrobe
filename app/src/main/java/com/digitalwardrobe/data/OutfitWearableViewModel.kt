package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class OutfitWearableViewModel(application: Application) : ViewModel() {

    private val repository: OutfitWearableRepository

    init {
        val dao = DigitalWardrobeRoomDatabase.getDatabase(application).outfitWearableDao()
        repository = OutfitWearableRepository(application, dao)
    }

    suspend fun insert(outfitWearable: OutfitWearable) : Long {
        return repository.insert(outfitWearable)
    }

    suspend fun getWearablesForOutfit(outfitId: Long): List<OutfitWearable?> {
        return repository.getWearablesForOutfit(outfitId)
    }

    suspend fun getWearableForOutfit(outfitId: Long, wearableId: Long): OutfitWearable? {
        return repository.getWearableForOutfit(outfitId, wearableId)
    }

    suspend fun update(outfitWearable: OutfitWearable) {
        repository.update(outfitWearable)
    }

    suspend fun delete(outfitWearable: OutfitWearable) {
        repository.delete(outfitWearable)
    }
}

class OutfitWearableViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitWearableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutfitWearableViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}