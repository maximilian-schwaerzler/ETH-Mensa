package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Offer.Menu
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility

@Database(
    entities = [Facility::class, Offer::class, Menu::class, Menu.MenuPrice::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MensaDatabase : RoomDatabase() {
    abstract fun facilityDao(): FacilityDao
    abstract fun menuDao(): MenuDao
}