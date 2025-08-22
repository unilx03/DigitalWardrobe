package com.digitalwardrobe.data

import android.app.Application

class OutfitWearableRepository(app: Application, private val dao: OutfitWearableDao) {
    suspend fun getAllOutfitWearables() : List<OutfitWearable> {
        return dao.getAllOutfitWearables()
    }

    suspend fun insert(outfitWearable: OutfitWearable) : Long {
        return dao.insert(outfitWearable)
    }

    suspend fun getWearablesForOutfit(outfitId: Long): List<OutfitWearable?> {
        return dao.getWearablesForOutfit(outfitId)
    }

    suspend fun getWearableForOutfit(outfitId: Long, wearableId: Long): OutfitWearable? {
        return dao.getWearableForOutfit(outfitId, wearableId)
    }

    suspend fun update(outfitWearable: OutfitWearable) {
        dao.update(outfitWearable)
    }

    suspend fun delete(outfitWearable: OutfitWearable) {
        dao.delete(outfitWearable)
    }
}