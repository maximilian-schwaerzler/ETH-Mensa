package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import android.content.Context
import android.util.Log
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.CustomerGroup as DBCustomerGroup
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility as DBFacility

/**
 * Fetches the facility information from the API and saves it in the database
 *
 * @param context Application context
 */
suspend fun updateFacilityInfoDB(context: Context) = withContext(Dispatchers.IO) {
    val db = MensaDatabase.getInstance(context)
    val facilityDao = db.facilityDao()
    val customerGroupDao = db.customerGroupDao()

    // Get the facility information from the API
    val mensaIds = context.resources.getIntArray(R.array.id_mensas_with_customer_groups)
    for (facilityId in mensaIds) {
        val localFacilityInfo = fetchFacilityInformation(facilityId)

        // If localFacilityInfo is null, skip this facility
        if (localFacilityInfo == null) {
            Log.w("updateFacilityInfoDB", "Facility with ID=$facilityId could not be fetched")
            continue
        }

        Log.d("updateFacilityInfoDB", "Facility with ID=$facilityId fetched\n$localFacilityInfo")

        // Save the facility information in the database
        facilityDao.insertAll(
            DBFacility(
                id = localFacilityInfo.facilityId,
                name = localFacilityInfo.name,
                location = localFacilityInfo.locationByBuilding
            )
        )

        Log.d("updateFacilityInfoDB", "Facility with ID=$facilityId saved in the database")

        // Save the customer groups in the database
        for ((groupId, groupName) in localFacilityInfo.customerGroups) {
            customerGroupDao.insertAll(
                DBCustomerGroup(
                    facilityId = localFacilityInfo.facilityId,
                    groupId = groupId,
                    name = groupName
                )
            )
        }

        Log.d(
            "updateFacilityInfoDB",
            "Customer groups for facility with ID=$facilityId saved in the database"
        )
    }
}