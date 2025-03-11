package com.github.maximilianschwaerzler.ethuzhmensa.data.utils

import android.content.Context
import com.github.maximilianschwaerzler.ethuzhmensa.data.LocalFacilityInfo
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

suspend fun saveDailyMenu(context: Context, facility: Facility, weekOf: LocalDate) = withContext(Dispatchers.IO) {
    val db = MensaDatabase.getInstance(context)
    val facilityDao = db.facilityDao()
}