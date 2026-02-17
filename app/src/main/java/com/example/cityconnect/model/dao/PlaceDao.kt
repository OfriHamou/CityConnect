package com.example.cityconnect.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cityconnect.model.schemas.PlaceEntity

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places WHERE category = :category ORDER BY name ASC")
    fun observeByCategory(category: String): LiveData<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PlaceEntity>)

    @Query("DELETE FROM places WHERE category = :category")
    suspend fun deleteByCategory(category: String)

    @Transaction
    suspend fun replaceCategory(category: String, items: List<PlaceEntity>) {
        deleteByCategory(category)
        insertAll(items)
    }
}

