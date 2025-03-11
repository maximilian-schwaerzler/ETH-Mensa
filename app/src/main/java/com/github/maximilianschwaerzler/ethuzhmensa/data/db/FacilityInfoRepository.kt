package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FacilityInfoRepository @Inject constructor(@ApplicationContext val context: Context) {
    private val db = MensaDatabase.getInstance(context)
    private val facilityDao = db.facilityDao()

    fun observeAllFacilities() = facilityDao.observeAllWithCustomerGroups()
    suspend fun getAllFacilities() =
        withContext(Dispatchers.IO) { facilityDao.getAllWithCustomerGroups() }
}