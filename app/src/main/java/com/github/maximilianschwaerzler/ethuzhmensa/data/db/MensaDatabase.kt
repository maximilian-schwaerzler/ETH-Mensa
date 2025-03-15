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

    companion object {
        private const val DATABASE_NAME = "mensas.db"

        /**
         * As we need only one instance of db in our app will use to store
         * This is to avoid memory leaks in android when there exist multiple instances of db
         */
        @Volatile
        private var INSTANCE: MensaDatabase? = null

        fun getInstance(context: Context): MensaDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MensaDatabase::class.java,
                        DATABASE_NAME
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}