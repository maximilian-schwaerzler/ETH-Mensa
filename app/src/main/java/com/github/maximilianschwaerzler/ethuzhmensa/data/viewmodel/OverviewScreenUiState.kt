package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices

data class OverviewScreenUiState(
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val facilitiesWithOffers: List<Pair<Facility, OfferWithPrices?>> = emptyList(),
    val error: String? = null,
)
