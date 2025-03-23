package com.github.maximilianschwaerzler.ethuzhmensa.data

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices

data class OverviewScreenUiState(
    val isLoading: Boolean = false,
    val offers: List<OfferWithPrices> = emptyList(),
    val facilities: List<Facility> = emptyList(),
)
