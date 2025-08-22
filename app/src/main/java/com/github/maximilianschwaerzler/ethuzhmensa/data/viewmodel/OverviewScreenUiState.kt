/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices

/**
 * Data class representing the UI state for the overview screen.
 *
 * @property isInitialLoading Indicates if the initial loading is in progress.
 * @property isRefreshing Indicates if a refresh operation is in progress.
 * @property facilitiesWithOffers List of pairs of facilities and their associated offers with prices.
 * @property error An optional error message to be displayed in the UI.
 */
data class OverviewScreenUiState(
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val facilitiesWithOffers: List<Pair<Facility, OfferWithPrices?>> = emptyList(),
    val error: String? = null,
)
