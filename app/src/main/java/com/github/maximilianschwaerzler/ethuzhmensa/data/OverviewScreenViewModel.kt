package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import com.github.maximilianschwaerzler.ethuzhmensa.data2.MenuRepository2
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository,
    private val menuRepository: MenuRepository2,
    private val facilityDao: FacilityDao,
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext val appContext: Context,
) : ViewModel() {
//    private val _uiState = MutableStateFlow<OverviewScreenUiState>(OverviewScreenUiState(true))
//    val uiState = _uiState
//        .onStart { refreshData() }
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5_000),
//            OverviewScreenUiState(true)
//        )

    private val offers = MutableStateFlow<List<OfferWithPrices>>(emptyList())
    private val facilities = MutableStateFlow<List<Facility>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val facilitiesWithOffers = facilities.combine(offers) { facilities, offers ->
        facilities.map { facility ->
            facility to offers.find { it.offer.facilityId == facility.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    fun refreshData() = viewModelScope.launch {
        _isLoading.emit(true)
        supervisorScope {
            launch {
                saveFacilityInfoToDB(appContext, facilityDao)
                val newFacilities = facilityDao.getAll()
                Log.d("OverviewScreenViewModel", "New facilities: $newFacilities")
                facilities.emit(newFacilities)
            }
            launch {
                val newOffers = menuRepository.getOffersForDate(LocalDate.of(2025, 3, 18))
                Log.d("OverviewScreenViewModel", "New offers: $newOffers")
                offers.emit(newOffers)
            }
        }
        _isLoading.emit(false)
    }

//    private val _offersToday = MutableStateFlow<List<OfferWithPrices>>(emptyList())
//    private val facilities = facilityInfoRepo.observeAllFacilities()
//
//    val facilitiesWithOffers = facilities.combine(_offersToday) { facilities, offers ->
//        facilities.map { facility ->
//            facility to offers.find { it.offer.facilityId == facility.id }
//        }
//    }.onStart {
//        _offersToday.emit(
//            menuRepository.getAllOffersForDateFlow(LocalDate.of(2025, 3, 19)).first()
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())
//
//    private val _isRefreshing = MutableStateFlow(false)
//    val isRefreshing = _isRefreshing.asStateFlow()
//
//    suspend fun shouldUpdateFacilityInfo(): Boolean {
//        val lastFacilityInfoUpdate = dataStoreManager.lastFacilityFetchDate.firstOrNull()
//        if (lastFacilityInfoUpdate == LocalDate.MIN || lastFacilityInfoUpdate == null) {
//            Log.d(
//                "OverviewScreenViewModel",
//                "No facility info update time found, fetching data from the net"
//            )
//            return true
//        } else if (LocalDate.now().minusDays(
//                appContext.resources.getInteger(R.integer.config_facility_info_stale_days)
//                    .toLong()
//            ).isAfter(lastFacilityInfoUpdate as ChronoLocalDate?)
//        ) {
//            Log.d(
//                "OverviewScreenViewModel",
//                "Facility info is older than 7 days, fetching data from the net"
//            )
//            return true
//        } else if (facilities.first().isEmpty()) {
//            Log.d("OverviewScreenViewModel", "No facility info found, fetching data from the net")
//            return true
//        }
//        Log.d("OverviewScreenViewModel", "Facility info is up to date")
//        return false
//    }
//
//    fun onRefresh() = viewModelScope.launch {
//        Log.d(
//            "OverviewScreenViewModel",
//            "Refreshing data. Today is ${LocalDate.now().toEpochDay()}"
//        )
//        _isRefreshing.emit(true)
//        supervisorScope {
//            if (shouldUpdateFacilityInfo()) {
//                launch {
//                    saveFacilityInfoToDB(appContext, facilityDao)
//                    dataStoreManager.updateLastFacilityFetchDate(LocalDate.now())
//                }
//            }
//            _offersToday.emit(
//                menuRepository.getAllOffersForDateFlow(LocalDate.of(2025, 3, 19)).first()
//            )
//        }
//        _isRefreshing.emit(false)
//    }
}