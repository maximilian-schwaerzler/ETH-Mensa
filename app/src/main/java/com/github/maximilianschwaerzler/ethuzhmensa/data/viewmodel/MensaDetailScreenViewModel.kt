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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for managing the state and data of the Mensa detail screen.
 *
 * @property facilityInfoRepo Repository for accessing facility information.
 * @property menuRepository Repository for accessing menu information.
 */
@HiltViewModel
class MensaDetailScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository,
    private val menuRepository: MenuRepository,
) : ViewModel() {

    private val _menus = MutableStateFlow<OfferWithPrices?>(null)
    val menus = _menus.asStateFlow()

    private val _facility = MutableStateFlow<Facility?>(null)
    val facility = _facility.asStateFlow()

    /**
     * Sets the favourite status of a facility.
     *
     * @param facilityId The ID of the facility to update.
     * @param isFavourite The new favourite status to set.
     */
    fun setFavourite(facilityId: Int, isFavourite: Boolean) {
        viewModelScope.launch {
            facilityInfoRepo.updateFavourite(facilityId, isFavourite)
        }
    }

    /**
     * Loads the facility information and menus for a given facility ID and date.
     *
     * @param facilityId The ID of the facility to load.
     * @param date The date for which to load the menus.
     */
    fun loadFacilityAndMenus(facilityId: Int, date: LocalDate) = try {
        viewModelScope.launch {
            launch {
                facilityInfoRepo.observeFacilityById(facilityId)
                    .collectLatest { _facility.emit(it) }
            }

            menuRepository.getOffer(facilityId, date).let { fetchedOffers ->
                if (fetchedOffers == null) {
                    Log.e(
                        "MensaDetailScreenViewModel",
                        "No offers found for facility $facilityId on date $date"
                    )
                    return@let
                }
                _menus.value = fetchedOffers
            }
        }
    } catch (e: IllegalStateException) {
        Log.e("MensaDetailScreenViewModel", "No internet connection", e)
    }
}