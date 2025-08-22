/*
 * Copyright (c) 2025 Maximilian Schwärzler
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Facility entity.
 */
@Dao
interface FacilityDao {
    @Query("SELECT * FROM Facility")
    suspend fun getAll(): List<Facility>

    @Query("SELECT * FROM Facility")
    fun observeAll(): Flow<List<Facility>>

    @Query("SELECT * FROM Facility WHERE id = :facilityId")
    fun observeFacilityById(facilityId: Int): Flow<Facility>

    @Query("SELECT * FROM Facility WHERE id = :facilityId")
    suspend fun getFacilityById(facilityId: Int): Facility

    @Query("SELECT * FROM Facility WHERE id IN (:facilityIds)")
    suspend fun loadAllByIds(facilityIds: IntArray): List<Facility>

    @Query("SELECT * FROM Facility WHERE location LIKE :location LIMIT 1")
    suspend fun findByLocation(location: String): Facility

    @Query("SELECT * FROM Facility WHERE favorite = 1")
    fun getFavouriteFacilities(): Flow<List<Facility>>

    @Upsert
    suspend fun insertAll(vararg facilities: Facility)

    @Query("UPDATE Facility SET favorite = :isFavourite WHERE id = :facilityId")
    suspend fun updateFavouriteStatus(facilityId: Int, isFavourite: Boolean)

    @Upsert
    suspend fun insert(facility: Facility)

    @Delete
    suspend fun delete(facility: Facility)

    @Query("DELETE FROM Facility WHERE id = :facilityId")
    suspend fun deleteById(facilityId: Int)
}