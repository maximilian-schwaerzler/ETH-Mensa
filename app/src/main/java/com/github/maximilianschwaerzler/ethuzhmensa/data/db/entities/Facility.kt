/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Facility(
    @PrimaryKey val id: Int,
    val name: String,
    val location: String,
)