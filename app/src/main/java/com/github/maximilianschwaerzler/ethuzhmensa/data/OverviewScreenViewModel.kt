package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val menuRepository: MenuRepository,
) : ViewModel() {

    private val offers = MutableStateFlow<List<OfferWithPrices>>(emptyList())
    private val facilities = MutableStateFlow<List<Facility>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val facilitiesWithOffers = facilities.combine(offers) { facilities, offers ->
        facilities.map { facility ->
            facility to offers.find { it.offer.facilityId == facility.id }
        }
    }
        .onStart { refreshData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    fun onPullToRefresh() {
        _isLoading.value = true
        refreshData()
        _isLoading.value = false
    }

    fun refreshData() =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                supervisorScope {
                    val facilityJob = launch {
                        try {
                            facilities.emit(facilityInfoRepo.getAllFacilities())
                        } catch (e: IllegalStateException) {
                            Log.d("OverviewScreenViewModel", "No internet connection", e)
                        }
                    }
                    val offerJob = launch {
                        try {
                            offers.emit(menuRepository.getOffersForDate(LocalDate.now()))
                        } catch (e: IllegalStateException) {
                            Log.w("OverviewScreenViewModel", "No internet connection", e)
                        }
                    }
                    facilityJob.join()
                    offerJob.join()
                }
            } catch (e: Exception) {
                Log.w("OverviewScreenViewModel", "Unexpected error", e)
            }
        }
}