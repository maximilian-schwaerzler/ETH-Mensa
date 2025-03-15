package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOffer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOfferWithPrices
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MenuDao {
    @Transaction
    @Query("SELECT * FROM DailyOffer WHERE facilityId = :facilityId AND date = :date LIMIT 1")
    suspend fun getOfferForFacilityDate(facilityId: Int, date: LocalDate): DailyOfferWithPrices

    @Transaction
    @Query("SELECT * FROM DailyOffer WHERE date = :date")
    suspend fun getAllOffersForDate(date: LocalDate): List<DailyOfferWithPrices>

    @Transaction
    @Query("SELECT * FROM DailyOffer WHERE date = :date")
    fun observeAllOffersForDate(date: LocalDate): Flow<List<DailyOfferWithPrices>>

    @Insert
    suspend fun insertOffer(dailyOffer: DailyOffer): Long

    @Insert
    suspend fun insertMenu(menu: DailyOffer.Menu): Long

    @Insert
    suspend fun insertPrice(price: DailyOffer.Menu.MenuPrice): Long

    @Query("DELETE FROM DailyOffer WHERE date < :date")
    suspend fun deleteOlderThan(date: LocalDate)
}