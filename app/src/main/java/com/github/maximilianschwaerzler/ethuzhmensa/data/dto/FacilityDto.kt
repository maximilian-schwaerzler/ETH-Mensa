/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.dto

/**
 * Data Transfer Object (DTO) representing a facility.
 */
data class FacilityDto(
    val facilityId: Int? = null,
    val facilityName: String? = null,
    val facilityLocationByBuilding: String? = null
)
