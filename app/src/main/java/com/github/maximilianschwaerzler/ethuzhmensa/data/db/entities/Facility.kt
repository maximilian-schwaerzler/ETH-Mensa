/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a facility entity in the database.
 */
@Entity
data class Facility(
    @PrimaryKey val id: Int,
    val name: String,
    val location: String,
    @ColumnInfo(defaultValue = "0") val favorite: Boolean = false,
)