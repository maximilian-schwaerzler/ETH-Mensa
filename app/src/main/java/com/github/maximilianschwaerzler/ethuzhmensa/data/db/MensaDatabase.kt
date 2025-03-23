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

@Database(
    entities = [Facility::class, Offer::class, Menu::class, Menu.MenuPrice::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = MensaDatabase.Migration1to2::class)
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