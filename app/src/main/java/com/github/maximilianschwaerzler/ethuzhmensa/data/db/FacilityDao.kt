package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Query("SELECT * FROM Facility")
    suspend fun getAll(): List<Facility>

    @Query("SELECT * FROM Facility")
    fun observeAll(): Flow<List<Facility>>

    @Query("SELECT * FROM Facility WHERE id = :facilityId")
    suspend fun getFacilityById(facilityId: Int): Facility

    @Query("SELECT * FROM Facility WHERE id IN (:facilityIds)")
    suspend fun loadAllByIds(facilityIds: IntArray): List<Facility>

    @Query("SELECT * FROM Facility WHERE location LIKE :location LIMIT 1")
    suspend fun findByLocation(location: String): Facility

    @Upsert
    suspend fun insertAll(vararg facilities: Facility)

    @Upsert
    suspend fun insert(facility: Facility)

    @Delete
    suspend fun delete(facility: Facility)

    @Query("DELETE FROM Facility WHERE id = :facilityId")
    suspend fun deleteById(facilityId: Int)
}