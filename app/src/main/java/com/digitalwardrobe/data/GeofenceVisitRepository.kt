package com.digitalwardrobe.data

class GeofenceVisitRepository(private val dao: GeofenceVisitDao) {

    suspend fun insert(visit : GeofenceVisit) {
        dao.insert(visit)
    }

    suspend fun countVisitsSince(requestId: String, fromTime : Long): Int {
        return dao.countVisitsSince(requestId, fromTime)
    }
}