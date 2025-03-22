package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveAllDailyMenusToDBConcurrent
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository,
    private val menuRepository: MenuRepository,
    private val menuDao: MenuDao,
    private val facilityDao: FacilityDao,
    @ApplicationContext val appContext: Context,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val offersToday = menuRepository.observeAllOffersForDate(LocalDate.now())
    private val facilities = facilityInfoRepo.observeAllFacilities()

    val facilitiesWithOffers = facilities.combine(offersToday) { facilities, offers ->
        facilities.map { facility ->
            facility to offers.find { it.dailyOffer.facilityId == facility.id }
        }
    }.stateIn(viewModelScope, started = SharingStarted.Lazily, listOf())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    suspend fun shouldUpdateFacilityInfo(): Boolean {
        val lastFacilityInfoUpdate = dataStoreManager.lastFacilityFetchDate.firstOrNull()
        if (lastFacilityInfoUpdate == LocalDate.MIN || lastFacilityInfoUpdate == null) {
            Log.d(
                "OverviewScreenViewModel",
                "No facility info update time found, fetching data from the net"
            )
            return true
        } else if (LocalDate.now().minusDays(
                appContext.resources.getInteger(R.integer.config_facility_info_stale_days)
                    .toLong()
            ).isAfter(lastFacilityInfoUpdate as ChronoLocalDate?)
        ) {
            Log.d(
                "OverviewScreenViewModel",
                "Facility info is older than 7 days, fetching data from the net"
            )
            return true
        } else if (facilities.first().isEmpty()) {
            Log.d("OverviewScreenViewModel", "No facility info found, fetching data from the net")
            return true
        }
        Log.d("OverviewScreenViewModel", "Facility info is up to date")
        return false
    }

    suspend fun shouldUpdateMenus(): Boolean {
        val lastMenuSyncTime = dataStoreManager.lastMenuFetchDate.firstOrNull()
        if (lastMenuSyncTime == LocalDate.MIN || lastMenuSyncTime == null) {
            Log.d(
                "OverviewScreenViewModel",
                "No menu update time found, fetching data from the net"
            )
            return true
        } else if (lastMenuSyncTime.isBefore(LocalDate.now())) {
            Log.d(
                "OverviewScreenViewModel",
                "Menu data is older than 1 day, fetching data from the net"
            )
            return true
        } else if (offersToday.first().isEmpty()) {
            Log.d("OverviewScreenViewModel", "No menu data found, fetching data from the net")
            return true
        }
        Log.d("OverviewScreenViewModel", "Menu data is up to date")
        return false
    }

    fun onRefresh() = viewModelScope.launch {
        Log.d(
            "OverviewScreenViewModel",
            "Refreshing data. Today is ${LocalDate.now().toEpochDay()}"
        )
        _isRefreshing.emit(true)
        supervisorScope {
            if (shouldUpdateFacilityInfo()) {
                launch {
                    saveFacilityInfoToDB(appContext, facilityDao)
                    dataStoreManager.setLastFacilityFetchDate(LocalDate.now())
                }
            }

            if (shouldUpdateMenus()) {
                launch {
                    saveAllDailyMenusToDBConcurrent(appContext, LocalDate.now(), menuDao)
                    dataStoreManager.setLastMenuFetchDate(LocalDate.now())
                }

                // BUG: This is a workaround for the issue that the "Pull to Refresh" animation sometimes does not disappear
                delay(10)
            }
        }
        _isRefreshing.emit(false)
    }

//    fun printAllFacilitiesToLog() = viewModelScope.launch {
//        Log.d("OverviewScreenViewModel", facilityInfoRepo.getAllFacilities().toString())
//    }

    fun updateFacilityInfoDBNet() = viewModelScope.launch {
        saveFacilityInfoToDB(appContext, facilityDao)
    }

    fun updateMenusDBNet(forWeek: LocalDate = LocalDate.now()) = viewModelScope.launch {
        saveAllDailyMenusToDBConcurrent(appContext, forWeek, menuDao)
    }

    fun deleteOlderThan(date: LocalDate = LocalDate.now()) = viewModelScope.launch {
        menuRepository.deleteOlderThan(date)
    }

//    fun reloadAllMenusForDate(date: LocalDate = LocalDate.now()) = viewModelScope.launch {
//        val newOffers = menuRepository.getAllOffersForDate(date)
//        _offers.emit(newOffers)
//    }

    fun purgeDB() = viewModelScope.launch {
        facilityInfoRepo.purgeDB()
    }
}