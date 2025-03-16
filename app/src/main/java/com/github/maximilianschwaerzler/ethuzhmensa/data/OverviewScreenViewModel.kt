package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityInfoRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveAllDailyMenusToDBConcurrent
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityInfoRepository,
    private val menuRepository: MenuRepository,
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
        val lastFacilityInfoUpdate = dataStoreManager.lastFacilitySyncTime.firstOrNull()
        if (lastFacilityInfoUpdate == 0L || lastFacilityInfoUpdate == null) {
            Log.d(
                "OverviewScreenViewModel",
                "No facility info update time found, fetching data from the net"
            )
            return true
        } else if (LocalDate.now().minusDays(
                appContext.resources.getInteger(R.integer.config_facility_info_stale_days)
                    .toLong()
            ) > LocalDate.ofEpochDay(lastFacilityInfoUpdate)
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
        val lastMenuSyncTime = dataStoreManager.lastMenuSyncTime.firstOrNull()
        if (lastMenuSyncTime == 0L || lastMenuSyncTime == null) {
            Log.d(
                "OverviewScreenViewModel",
                "No menu update time found, fetching data from the net"
            )
            return true
        } else if (LocalDate.ofEpochDay(lastMenuSyncTime).isBefore(LocalDate.now())) {
            Log.d(
                "OverviewScreenViewModel",
                "Menu data is older than 1 day, fetching data from the net"
            )
            return true
        }
        Log.d("OverviewScreenViewModel", "Menu data is up to date")
        return false
    }

    fun onRefresh() = viewModelScope.launch {
        Log.d("OverviewScreenViewModel", "Refreshing data. Today is ${LocalDate.now().toEpochDay()}")
        _isRefreshing.emit(true)
        coroutineScope {
            if (shouldUpdateFacilityInfo()) {
                launch {
                    saveFacilityInfoToDB(appContext)
                    dataStoreManager.setLastFacilitySyncTime(LocalDate.now().toEpochDay())
                }
            }

            if (shouldUpdateMenus()) {
                launch {
                    saveAllDailyMenusToDBConcurrent(appContext, LocalDate.now())
                    dataStoreManager.setLastMenuSyncTime(LocalDate.now().toEpochDay())
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
        saveFacilityInfoToDB(appContext)
    }

    fun updateMenusDBNet(forWeek: LocalDate = LocalDate.now()) = viewModelScope.launch {
        saveAllDailyMenusToDBConcurrent(appContext, forWeek)
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