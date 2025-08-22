/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.mapJsonObjectToFacility
import com.github.maximilianschwaerzler.ethuzhmensa.network.isConnected
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitFacilityService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository for managing facility data, including fetching from the network and caching in the database.
 */
class FacilityRepository @Inject constructor(
    private val facilityService: CookpitFacilityService,
    private val facilityDao: FacilityDao,
    private val dataStoreManager: DataStoreManager,
    private val connMgr: ConnectivityManager,
    @ApplicationContext private val appContext: Context
) {

    /**
     * Updates the facility information in the local database by fetching data from the network.
     * This function checks for internet connectivity before attempting to fetch data.
     * If there is no internet connection, it logs a warning and throws an IllegalStateException.
     *
     * @throws IllegalStateException if there is no internet connection
     */
    @Throws(IllegalStateException::class)
    suspend fun updateFacilityInfoDB() {
        if (!connMgr.isConnected()) {
            Log.w("FacilityRepository", "No internet connection, skipping facility update")
            throw IllegalStateException("No internet connection")
        }
        supervisorScope {
            for (facilityId in appContext.resources.getIntArray(R.array.id_mensas_with_customer_groups)) {
                launch {
                    val response = facilityService.fetchFacility(facilityId)
                    if (response.isSuccessful) {
                        val facility = mapJsonObjectToFacility(response.body()!!)
                        facilityDao.insert(
                            Facility(
                                id = facility.facilityId!!,
                                name = facility.facilityName!!,
                                location = facility.facilityLocationByBuilding!!
                            )
                        )
                    } else {
                        Log.e(
                            "FacilityRepository",
                            "Failed to fetch facility info for facility $facilityId"
                        )
                    }
                }
            }
        }
        dataStoreManager.updateLastFacilityFetchDate()
    }

    /**
     * Retrieves all facilities from the local database. If a facility is not found, it attempts to
     * update the database from the network.
     *
     * @return A list of all facilities.
     * @throws IllegalStateException if a facility cannot be found after attempting to update from the network.
     */
    @Throws(IllegalStateException::class)
    suspend fun getAllFacilities(): List<Facility> {
        val array = appContext.resources.getIntArray(R.array.id_mensas_with_customer_groups)
        return array.map {
            try {
                facilityDao.getFacilityById(it)
            } catch (e: IllegalStateException) {
                // Assume that no facility was found, so we fetch it from the API
                Log.d(
                    "FacilityRepository", "No facility found for id $it, updating from API", e
                )
                updateFacilityInfoDB()
                try {
                    facilityDao.getFacilityById(it)
                } catch (e: IllegalStateException) {
                    Log.e(
                        "FacilityRepository", "Failed to fetch facility info for facility $it", e
                    )
                    throw e
                }
            }
        }
    }

    /**
     * Observes a facility by its ID from the local database.
     */
    fun observeFacilityById(facilityId: Int) = facilityDao.observeFacilityById(facilityId)

    /**
     * Retrieves a facility by its ID from the local database. If the facility is not found, it attempts to
     * update the database from the network.
     *
     * @param facilityId The ID of the facility to retrieve.
     * @return The facility with the specified ID.
     * @throws IllegalStateException if the facility cannot be found after attempting to update from the network.
     */
    @Throws(IllegalStateException::class)
    suspend fun getFacilityById(facilityId: Int): Facility {
        return try {
            facilityDao.getFacilityById(facilityId)
        } catch (e: IllegalStateException) {
            // Assume that no facility was found, so we fetch it from the API
            Log.d(
                "FacilityRepository", "No facility found for id $facilityId, updating from API", e
            )
            updateFacilityInfoDB()
            try {
                facilityDao.getFacilityById(facilityId)
            } catch (e: IllegalStateException) {
                Log.e(
                    "FacilityRepository",
                    "Failed to fetch facility info for facility $facilityId",
                    e
                )
                throw e
            }
        }
    }

    /**
     * Updates the favourite status of a facility in the local database.
     *
     * @param id The ID of the facility to update.
     * @param newFavourite The new favourite status to set.
     */
    suspend fun updateFavourite(id: Int, newFavourite: Boolean) = withContext(Dispatchers.IO) {
        facilityDao.updateFavouriteStatus(id, newFavourite)
    }
}