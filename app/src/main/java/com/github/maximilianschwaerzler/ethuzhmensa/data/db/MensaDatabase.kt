/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer.Menu

/**
 * The Room database for the Mensa application.
 * It includes entities for facilities, offers, menus, and menu prices.
 * The database version is 3, with auto migrations defined from version 1 to 2 and from 2 to 3.
 */
@Database(
    entities = [Facility::class, Offer::class, Menu::class, Menu.MenuPrice::class],
    version = 3,
    autoMigrations = [
        AutoMigration(1, 2, spec = MensaDatabase.Migration1to2::class),
        AutoMigration(2, 3)
    ]
)
@TypeConverters(Converters::class)
abstract class MensaDatabase : RoomDatabase() {
    abstract fun facilityDao(): FacilityDao
    abstract fun menuDao(): MenuDao

    @RenameTable(
        fromTableName = "DailyOffer",
        toTableName = "Offer"
    )
    class Migration1to2 : AutoMigrationSpec
}