package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.R
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityDao
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.saveFacilityInfoToDB
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository2
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository,
    private val menuRepository: MenuRepository2,
    private val facilityDao: FacilityDao,
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext val appContext: Context,
) : ViewModel() {

    private val offers = MutableStateFlow<List<OfferWithPrices>>(emptyList())
    private val facilities = MutableStateFlow<List<Facility>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val facilitiesWithOffers = facilities.combine(offers) { facilities, offers ->
        facilities.map { facility ->
            facility to offers.find { it.offer.facilityId == facility.id }
        }
    }.onStart { refreshData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    fun refreshData() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.emit(true)
        supervisorScope {
            launch {
                if (shouldUpdateFacilityInfo()) {
                    saveFacilityInfoToDB(appContext, facilityDao)
                    dataStoreManager.updateLastFacilityFetchDate()
                }
                facilities.emit(facilityInfoRepo.getAllFacilities())
            }
            launch {
                offers.emit(menuRepository.getOffersForDate(LocalDate.now()))
            }
        }
        _isLoading.emit(false)
    }

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
}