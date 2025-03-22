package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOffer
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.DailyOffer.Menu
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility

@Database(
    entities = [Facility::class, DailyOffer::class, Menu::class, Menu.MenuPrice::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MensaDatabase : RoomDatabase() {
    abstract fun facilityDao(): FacilityDao
    abstract fun menuDao(): MenuDao
}