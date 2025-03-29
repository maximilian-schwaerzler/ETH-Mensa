/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.viewmodel

import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices

data class OverviewScreenUiState(
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val facilitiesWithOffers: List<Pair<Facility, OfferWithPrices?>> = emptyList(),
    val error: String? = null,
)
