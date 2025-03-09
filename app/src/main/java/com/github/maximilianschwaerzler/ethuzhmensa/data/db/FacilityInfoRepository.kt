package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import android.content.Context
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.updateFacilityInfoDB
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FacilityInfoRepository @Inject constructor(@ApplicationContext val context: Context) {
    val db = MensaDatabase.getInstance(context)
    suspend fun getAllFacilityInfos(forceNetworkFetch: Boolean = false): List<FacilityWithCustomerGroups> =
        withContext(Dispatchers.IO) {
            if (forceNetworkFetch) {
                updateFacilityInfoDB(context)
                return@withContext db.facilityDao().getAllWithCustomerGroups()
            }

            val facilityInfos = db.facilityDao().getAllWithCustomerGroups()
            if (!facilityInfos.isEmpty()) {
                Log.d(
                    "FacilityInfoRepository",
                    "FacilityInfos are cached, not fetching from network"
                )
                return@withContext facilityInfos
            } else {
                Log.w(
                    "FacilityInfoRepository",
                    "FacilityInfos are not cached, fetching from network"
                )
                // If the facilities are not cached, fetch them from the network
                updateFacilityInfoDB(context)
                return@withContext db.facilityDao().getAllWithCustomerGroups()
            }
        }
}