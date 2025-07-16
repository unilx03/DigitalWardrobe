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
    val allWearables: LiveData<List<Wearable>>

    init {
        val dao = WearableRoomDatabase.getDatabase(application).wearableDao()
        repository = WearableRepository(application, dao)
        allWearables = repository.allWearables
    }

    suspend fun insert(wearable: Wearable) : Long {
        return repository.insert(wearable)
    }

    suspend fun updateWearable(wearable: Wearable) {
        repository.update(wearable)
    }

    suspend fun delete(wearable: Wearable) {
        repository.delete(wearable)
    }

    fun getWearableById(id: Long): LiveData<Wearable> {
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