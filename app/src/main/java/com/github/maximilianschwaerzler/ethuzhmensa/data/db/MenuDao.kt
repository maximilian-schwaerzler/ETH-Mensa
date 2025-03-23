package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MenuDao {
    @Transaction
    @Query("SELECT * FROM Offer WHERE facilityId = :facilityId AND date = :date LIMIT 1")
    suspend fun getOfferForFacilityDate(facilityId: Int, date: LocalDate): OfferWithPrices

    @Transaction
    @Query("SELECT * FROM Offer WHERE date = :date")
    suspend fun getAllOffersForDate(date: LocalDate): List<OfferWithPrices>

    @Transaction
    @Query("SELECT * FROM Offer WHERE date = :date")
    fun observeAllOffersForDate(date: LocalDate): Flow<List<OfferWithPrices>>

    @Insert
    suspend fun insertOffer(offer: Offer): Long

    @Insert
    suspend fun insertMenu(menu: Offer.Menu): Long

    @Insert
    suspend fun insertPrice(price: Offer.Menu.MenuPrice): Long

    @Query("DELETE FROM Offer WHERE date < :date")
    suspend fun deleteOlderThan(date: LocalDate)
}