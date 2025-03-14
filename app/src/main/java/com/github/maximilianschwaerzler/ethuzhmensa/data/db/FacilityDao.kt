package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Query("SELECT * FROM Facility")
    suspend fun getAllWithCustomerGroups(): List<Facility>

    @Query("SELECT * FROM Facility")
    fun observeAllWithCustomerGroups(): Flow<List<Facility>>

    @Query("SELECT * FROM Facility WHERE id IN (:facilityIds)")
    suspend fun loadAllByIds(facilityIds: IntArray): List<Facility>

    @Query("SELECT * FROM Facility WHERE location LIKE :location LIMIT 1")
    suspend fun findByLocation(location: String): Facility

    @Upsert
    suspend fun insertAll(vararg facilities: Facility)

    @Delete
    suspend fun delete(facility: Facility)

    @Query("DELETE FROM Facility WHERE id = :facilityId")
    suspend fun deleteById(facilityId: Int)
}