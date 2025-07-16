package com.digitalwardrobe.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Query

class OutfitWearableRepository(app: Application, private val dao: OutfitWearableDao) {
    val allOutfitWearables: LiveData<List<OutfitWearable>> = dao.getAllOutfitWearables()

    suspend fun insert(outfitWearable: OutfitWearable) : Long {
        return dao.insert(outfitWearable)
    }

    fun getWearablesForOutfit(outfitId: Long): LiveData<List<OutfitWearable>> {
        return dao.getWearablesForOutfit(outfitId)
    }

    suspend fun update(outfitWearable: OutfitWearable) {
        dao.update(outfitWearable)
    }

    suspend fun delete(outfitWearable: OutfitWearable) {
        dao.delete(outfitWearable)
    }
}