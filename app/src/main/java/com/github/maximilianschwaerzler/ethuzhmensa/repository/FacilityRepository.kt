package com.github.maximilianschwaerzler.ethuzhmensa.repository

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FacilityRepository @Inject constructor(
    private val db: MensaDatabase
) {
    private val facilityDao = db.facilityDao()

    fun observeAllFacilities() = facilityDao.observeAll()
    suspend fun getAllFacilities() = facilityDao.getAll()

    suspend fun getFacilityById(facilityId: Int) = withContext(Dispatchers.IO) {
        facilityDao.loadById(facilityId)
    }

    suspend fun purgeDB() = withContext(Dispatchers.IO) { db.clearAllTables() }
}