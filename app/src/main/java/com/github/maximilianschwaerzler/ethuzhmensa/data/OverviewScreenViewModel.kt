package com.github.maximilianschwaerzler.ethuzhmensa.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data2.MenuRepository2
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val menuRepository: MenuRepository2,
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

    fun refreshData() = viewModelScope.launch {
        _isLoading.emit(true)
        supervisorScope {
            launch(Dispatchers.IO) {
//                saveFacilityInfoToDB(appContext, facilityDao)
                facilities.emit(facilityInfoRepo.getAllFacilities())
            }
            launch(Dispatchers.IO) {
                offers.emit(menuRepository.getOffersForDate(LocalDate.now()))
            }
        }
        _isLoading.emit(false)
    }
}