package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Transaction
    @Query("SELECT * FROM Facility")
    suspend fun getAllWithCustomerGroups(): List<FacilityWithCustomerGroups>

    @Transaction
    @Query("SELECT * FROM Facility")
    fun observeAllWithCustomerGroups(): Flow<List<FacilityWithCustomerGroups>>

    @Transaction
    @Query("SELECT * FROM Facility WHERE facilityId IN (:facilityIds)")
    suspend fun loadAllByIds(facilityIds: IntArray): List<FacilityWithCustomerGroups>

    @Transaction
    @Query("SELECT * FROM Facility WHERE location LIKE :location LIMIT 1")
    suspend fun findByLocation(location: String): FacilityWithCustomerGroups

    @Upsert
    suspend fun insertAll(vararg facilities: Facility)

    @Delete
    suspend fun delete(facility: Facility)

    @Query("DELETE FROM Facility WHERE facilityId = :facilityId")
    suspend fun deleteById(facilityId: Int)
}