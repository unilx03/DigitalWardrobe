package com.digitalwardrobe.data

import android.app.Application

class DailyOutfitRepository(app: Application, private val dao: DailyOutfitDao) {
    suspend fun getAllDailyOutfits() : List<DailyOutfit> {
        return dao.getAllDailyOutfits()
    }

    suspend fun insert(outfit: DailyOutfit) : Long {
        return dao.insert(outfit)
    }

    suspend fun update(outfit: DailyOutfit) {
        dao.update(outfit)
    }

    suspend fun delete(outfit: DailyOutfit) {
        dao.delete(outfit)
    }

    suspend fun getDailyOutfitByDate(date: String): DailyOutfit? {
        return dao.getDailyOutfitByDate(date)
    }
}