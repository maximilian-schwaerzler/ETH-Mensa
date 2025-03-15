package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityInfoRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveAllDailyMenusToDBConcurrent
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class DebugScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityInfoRepository,
    private val menuRepository: MenuRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _offers = MutableStateFlow(emptyList<DailyOfferWithPrices>())
    val offers = _offers.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val facilities = facilityInfoRepo.observeAllFacilities()
        .stateIn(viewModelScope, started = SharingStarted.Companion.WhileSubscribed(1000), listOf())

    init {
        reloadAllMenusForDate(LocalDate.of(2025, 3, 12))
    }

    fun refreshTrigger() {
        _isRefreshing.tryEmit(true)
        viewModelScope.launch {
            measureTimeMillis {
                saveAllDailyMenusToDBConcurrent(context, LocalDate.now())
            }.let { Log.d("OverviewScreen", "Menus loaded in $it ms") }
            reloadAllMenusForDate(LocalDate.of(2025, 3, 12))
            _isRefreshing.emit(false)
        }
    }

    fun printAllFacilities() = viewModelScope.launch {
        Log.d("OverviewScreenViewModel", facilityInfoRepo.getAllFacilities().toString())
    }

    fun updateFacilityInfoDBNet() = viewModelScope.launch {
        saveFacilityInfoToDB(context)
    }

    fun updateMenusDBNet(forWeek: LocalDate = LocalDate.now()) = viewModelScope.launch {
        saveAllDailyMenusToDBConcurrent(context, forWeek)
    }

    fun deleteOlderThanToday() = viewModelScope.launch {
        menuRepository.deleteOlderThan(LocalDate.now())
    }

    fun reloadAllMenusForToday() = viewModelScope.launch {
        val date = LocalDate.now()
        val newOffers = menuRepository.getAllOffersForDate(date)
        _offers.emit(newOffers)
    }

    fun reloadAllMenusForDate(date: LocalDate) = viewModelScope.launch {
        val newOffers = menuRepository.getAllOffersForDate(date)
        _offers.emit(newOffers)
    }

    fun purgeDB() = viewModelScope.launch {
        facilityInfoRepo.purgeDB()
    }
}