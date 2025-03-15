package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FacilityInfoRepository @Inject constructor(
    private val db: MensaDatabase
) {
    private val facilityDao = db.facilityDao()

    fun observeAllFacilities() = facilityDao.observeAll()
    suspend fun getAllFacilities() =
        withContext(Dispatchers.IO) { facilityDao.getAll() }

    suspend fun purgeDB() = withContext(Dispatchers.IO) { db.clearAllTables() }
}