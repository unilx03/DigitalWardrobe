package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class WearableViewModel(application: Application) : ViewModel() {

    private val repository: WearableRepository

    init {
        val dao = DigitalWardrobeRoomDatabase.getDatabase(application).wearableDao()
        repository = WearableRepository(application, dao)
    }

    suspend fun getAllWearables() : List<Wearable> {
        return repository.getAllWearables()
    }

    suspend fun insert(wearable: Wearable) : Long {
        return repository.insert(wearable)
    }

    suspend fun update(wearable: Wearable) {
        repository.update(wearable)
    }

    suspend fun delete(wearable: Wearable) {
        repository.delete(wearable)
    }

    suspend fun getWearableById(id: Long): Wearable? {
        return repository.getWearableById(id)
    }
}

class WearableViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WearableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WearableViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}