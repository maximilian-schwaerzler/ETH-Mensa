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

@HiltViewModel
class MensaDetailScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository,
    private val menuRepository: MenuRepository,
) : ViewModel() {

    private val _menus = MutableStateFlow<OfferWithPrices?>(null)
    val menus = _menus.asStateFlow()

    private val _facility = MutableStateFlow<Facility?>(null)
    val facility = _facility.asStateFlow()

    fun setFavourite(facilityId: Int, isFavourite: Boolean) {
        viewModelScope.launch {
            facilityInfoRepo.updateFavourite(facilityId, isFavourite)
        }
    }

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