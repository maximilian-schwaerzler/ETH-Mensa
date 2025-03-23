package com.github.maximilianschwaerzler.ethuzhmensa.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.github.maximilianschwaerzler.ethuzhmensa.data.DataStoreManager
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveAllDailyMenusToDBConcurrent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val menuDao: MenuDao,
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext private val appContext: Context
) {
    @Deprecated("Use getAllOffersForDateFlow instead")
    fun observeAllOffersForDate(date: LocalDate) = menuDao.observeAllOffersForDate(date)
    suspend fun getOfferForFacilityDate(facilityId: Int, date: LocalDate) =
        menuDao.getOfferForFacilityDate(facilityId, date)

    @Deprecated("Use getAllOffersForDateFlow instead")
    suspend fun getAllOffersForDate(date: LocalDate) = menuDao.getAllOffersForDate(date)

    @WorkerThread
    fun getAllOffersForDateFlow(date: LocalDate) = flow<List<OfferWithPrices>> {
        val lastMenuFetchDate = dataStoreManager.lastMenuFetchDate.first()
        Log.d("MenuRepository", "Last menu fetch date: $lastMenuFetchDate")
        emit(menuDao.getAllOffersForDate(date))
        if (lastMenuFetchDate == LocalDate.MIN || lastMenuFetchDate.get(WEEK_OF_WEEK_BASED_YEAR) != LocalDate.now()
                .get(WEEK_OF_WEEK_BASED_YEAR)
        ) {
            Log.d("MenuRepository", "Fetching new menus from API")
            saveAllDailyMenusToDBConcurrent(appContext, LocalDate.now(), menuDao)
            dataStoreManager.updateLastMenuFetchDate(LocalDate.now())
            emit(menuDao.getAllOffersForDate(date))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteOlderThan(date: LocalDate) = menuDao.deleteOlderThan(date)
}