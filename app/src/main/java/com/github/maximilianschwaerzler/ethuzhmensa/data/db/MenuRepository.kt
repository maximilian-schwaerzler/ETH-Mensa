package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import java.time.LocalDate
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val db: MensaDatabase
) {
    private val menuDao = db.menuDao()

    fun observeAllOffersForDate(date: LocalDate) = menuDao.observeAllOffersForDate(date)
    suspend fun getOfferForFacilityDate(facilityId: Int, date: LocalDate) = menuDao.getOfferForFacilityDate(facilityId, date)
    suspend fun getAllOffersForDate(date: LocalDate) = menuDao.getAllOffersForDate(date)
    suspend fun deleteOlderThan(date: LocalDate) = menuDao.deleteOlderThan(date)
}