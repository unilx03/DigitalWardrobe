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

    fun insert(wearable: Wearable) = viewModelScope.launch {
        repository.insert(wearable)
    }

    fun delete(wearable: Wearable) = viewModelScope.launch {
        repository.delete(wearable)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
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

/*
class WearableViewModel(private val repository: WearableRepository) : ViewModel() {

    val allWearables: LiveData<List<Wearable>> = repository.listOfWearable

    fun getAllWearables(): LiveData<List<Wearable>> {
        return repository.getAllWearables()
    }

    fun insertWearable (wearable: Wearable) {
        repository.insertWearable(wearable)
    }

    fun onEvent(event : WearableEvent) {
        when(event){
            is WearableEvent.DeleteWearable -> {
                viewModelScope.launch {
                    dao.deleteWearable(event.wearable)
                }
            }
            WearableEvent.HideDialog -> TODO()
            WearableEvent.SaveWearable -> TODO()
            is WearableEvent.SetTitle -> TODO()
            WearableEvent.ShowDialog -> TODO()
            is WearableEvent.SortWearables -> TODO()
        }
    }
}

*/