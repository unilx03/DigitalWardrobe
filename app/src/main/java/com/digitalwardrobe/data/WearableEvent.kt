package com.digitalwardrobe.data

sealed interface WearableEvent {
    object SaveWearable : WearableEvent
    data class SetTitle(val title : String) : WearableEvent

    object ShowDialog : WearableEvent
    object HideDialog : WearableEvent
    data class DeleteWearable(val wearable: Wearable) : WearableEvent
}