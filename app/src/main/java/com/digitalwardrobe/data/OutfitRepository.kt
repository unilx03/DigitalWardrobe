package com.digitalwardrobe.data

import android.app.Application

class OutfitRepository(app: Application, private val dao: OutfitDao) {
    suspend fun getAllOutfits() : List<Outfit> {
        return dao.getAllOutfits()
    }

    suspend fun insert(outfit: Outfit) : Long {
        return dao.insert(outfit)
    }

    suspend fun update(outfit: Outfit) {
        dao.update(outfit)
    }

    suspend fun delete(outfit: Outfit) {
        dao.delete(outfit)
    }

    suspend fun getOutfitById(id: Long): Outfit? {
        return dao.getOutfitById(id)
    }
}