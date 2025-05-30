package com.digitalwardrobe.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

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

class WearableViewModelFactory(private val repository: WearableRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WearableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WearableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}