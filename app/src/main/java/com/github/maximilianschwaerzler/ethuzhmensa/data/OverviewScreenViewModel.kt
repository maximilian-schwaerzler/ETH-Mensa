package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityInfoRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveAllDailyMenusToDBConcurrent
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityInfoRepository,
    private val menuRepository: MenuRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val offersToday = menuRepository.observeAllOffersForDate(LocalDate.of(2025, 3, 12))
//        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(1000), listOf())

    private val facilities = facilityInfoRepo.observeAllFacilities()
//        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(1000), listOf())

    val facilitiesWithOffers = facilities.combine(offersToday) { facilities, offers ->
        facilities.map { facility ->
            facility to offers.find { it.dailyOffer.facilityId == facility.id }
        }
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(1000), listOf())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

//    init {
//        reloadAllMenusForDate()
//    }

    fun onRefresh() = viewModelScope.launch {
        _isRefreshing.emit(true)
        coroutineScope {
            launch { saveAllDailyMenusToDBConcurrent(context, LocalDate.now()) }
            if (facilities.first().isEmpty()) {
                Log.d("OverviewScreenViewModel", "Facilities are empty, fetching them from the net")
                launch { saveFacilityInfoToDB(context) }
            }
        }
        _isRefreshing.emit(false)
    }

//    fun printAllFacilitiesToLog() = viewModelScope.launch {
//        Log.d("OverviewScreenViewModel", facilityInfoRepo.getAllFacilities().toString())
//    }

    fun updateFacilityInfoDBNet() = viewModelScope.launch {
        saveFacilityInfoToDB(context)
    }

    fun updateMenusDBNet(forWeek: LocalDate = LocalDate.now()) = viewModelScope.launch {
        saveAllDailyMenusToDBConcurrent(context, forWeek)
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