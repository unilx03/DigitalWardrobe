package com.digitalwardrobe.data

import android.app.Application

class WearableRepository(app: Application, private val dao: WearableDao) {
    suspend fun getAllWearables() : List<Wearable> {
        return dao.getAllWearables()
    }

    suspend fun insert(wearable: Wearable) : Long {
        return dao.insert(wearable)
    }

    suspend fun update(wearable: Wearable) {
        dao.update(wearable)
    }

    suspend fun delete(wearable: Wearable) {
        dao.delete(wearable)
    }

    suspend fun getWearableById(id: Long): Wearable? {
        return dao.getWearableById(id)
    }

    suspend fun getAllWearablesWithLocations(): List<Wearable> {
        return dao.getAllWearablesWithLocations()
    }
}