/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Overview screen, managing the state and data related to facilities and their offers.
 */
@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository,
    private val menuRepository: MenuRepository,
) : ViewModel() {

    private val offers = MutableStateFlow<List<OfferWithPrices>>(emptyList())
    private val facilities = MutableStateFlow<List<Facility>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    private val _isInitialLoading = MutableStateFlow(true)

    val uiState = combine(
        facilities,
        offers,
        _isLoading,
        _isInitialLoading
    ) { facilities, offers, isLoading, isInitialLoading ->
        val facilitiesWithOffers = facilities.map { facility ->
            facility to offers.find { it.offer.facilityId == facility.id }
        }
        OverviewScreenUiState(
            isRefreshing = isLoading,
            facilitiesWithOffers = facilitiesWithOffers,
            isInitialLoading = isInitialLoading
        )
    }
        .onStart {
            _isInitialLoading.value = true
            Log.d("OverviewScreenViewModel", "Initial loading start")
            refreshData().join()
            Log.d("OverviewScreenViewModel", "Initial loading end")
            _isInitialLoading.value = false
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OverviewScreenUiState())

    /**
     * Handles the pull-to-refresh action by refreshing the data and updating the loading state.
     * A delay is added to ensure the pull-to-refresh indicator is visible for a short duration.
     */
    fun onPullToRefresh() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.value = true
        refreshData()
        // Workaround for the pull to refresh indicator getting stuck
        delay(500)
        _isLoading.value = false
    }

    /**
     * Refreshes the facility and offer data by fetching from the respective repositories.
     * This function runs in the IO dispatcher and handles exceptions gracefully, logging any issues encountered.
     */
    private fun refreshData() =
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