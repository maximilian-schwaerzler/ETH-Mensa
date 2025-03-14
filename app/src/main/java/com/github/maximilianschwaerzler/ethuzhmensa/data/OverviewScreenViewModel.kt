package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityInfoRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveAllDailyMenusToDB
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
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
        loadAllMenusForToday()
    }

    fun refreshTrigger() {
        _isRefreshing.tryEmit(true)
        viewModelScope.launch {
            saveAllDailyMenusToDB(context, LocalDate.now())
            Log.d("OverviewScreen", "Menus loaded")
            loadAllMenusForToday()
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
        saveAllDailyMenusToDB(context, forWeek)
    }

    fun deleteOlderThanToday() = viewModelScope.launch {
        menuRepository.deleteOlderThan(LocalDate.now())
    }

    fun loadAllMenusForToday() = viewModelScope.launch {
        val date = LocalDate.now()
        val newOffers = menuRepository.getAllOffersForDate(date)
        _offers.emit(newOffers)
    }

    fun purgeDB() = viewModelScope.launch {
        facilityInfoRepo.purgeDB()
    }
}