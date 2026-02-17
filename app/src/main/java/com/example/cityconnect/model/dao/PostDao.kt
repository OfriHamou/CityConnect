package com.example.cityconnect.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cityconnect.model.schemas.PostEntity

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(posts: List<PostEntity>)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeAll(): LiveData<List<PostEntity>>

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deleteById(id: String)
}

