package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOffer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOfferWithPrices
import java.time.LocalDate

interface MenuDao {
    @Transaction
    @Query("SELECT * FROM DailyOffer WHERE facilityId = :facilityId AND date = :date LIMIT 1")
    suspend fun getOfferForFacilityDate(facilityId: Int, date: LocalDate): DailyOfferWithPrices

    @Transaction
    @Query("SELECT * FROM DailyOffer WHERE date = :date")
    suspend fun getAllOffersForDate(date: LocalDate): List<DailyOfferWithPrices>

    @Insert
    suspend fun insertAllOffers(vararg dailyOffers: DailyOffer)

    @Insert
    suspend fun insertAllMenus(vararg menus: DailyOffer.Menu)

    @Insert
    suspend fun insertAllPrices(vararg prices: DailyOffer.Menu.MenuPrice)

    @Query("DELETE FROM DailyOffer WHERE date < :date")
    suspend fun deleteOlderThan(date: LocalDate)
}