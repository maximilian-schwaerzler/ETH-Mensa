package com.github.maximilianschwaerzler.ethuzhmensa.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CustomerGroupDao {
    @Query("SELECT * FROM CustomerGroup WHERE groupId = :groupId")
    suspend fun getById(groupId: Int): CustomerGroup

    @Insert
    suspend fun insertAll(vararg customerGroups: CustomerGroup)

    @Delete
    suspend fun delete(customerGroup: CustomerGroup)

    @Query("DELETE FROM CustomerGroup WHERE groupId = :groupId")
    suspend fun deleteById(groupId: Int)
}