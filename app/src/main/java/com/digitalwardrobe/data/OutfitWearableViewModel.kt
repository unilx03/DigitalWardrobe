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
    val allOutfitWearables: LiveData<List<OutfitWearable>>

    init {
        val dao = OutfitWearableRoomDatabase.getDatabase(application).outfitWearableDao()
        repository = OutfitWearableRepository(application, dao)
        allOutfitWearables = repository.allOutfitWearables
    }

    suspend fun insert(outfitWearable: OutfitWearable) : Long {
        return repository.insert(outfitWearable)
    }

    fun getWearablesForOutfit(outfitId: Long): LiveData<List<OutfitWearable>> {
        return repository.getWearablesForOutfit(outfitId)
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