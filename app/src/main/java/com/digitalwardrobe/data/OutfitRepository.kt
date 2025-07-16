package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData

class OutfitRepository(app: Application, private val dao: OutfitDao) {
    val allOutfits: LiveData<List<Outfit>> = dao.getAllOutfits()

    suspend fun insert(outfit: Outfit) : Long {
        return dao.insert(outfit)
    }

    suspend fun update(outfit: Outfit) {
        dao.update(outfit)
    }

    suspend fun delete(outfit: Outfit) {
        dao.delete(outfit)
    }

    fun getOutfitById(id: Long): LiveData<Outfit?> {
        return dao.getOutfitById(id)
    }
}