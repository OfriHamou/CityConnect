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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(post: PostEntity)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    fun observeById(id: String): LiveData<PostEntity?>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: String): PostEntity?

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM posts WHERE ownerId = :ownerId")
    fun observeCountByOwner(ownerId: String): LiveData<Int>

    @Query("UPDATE posts SET ownerName = :ownerName, ownerAvatarUrl = :ownerAvatarUrl WHERE ownerId = :ownerId")
    suspend fun updateOwnerInfo(ownerId: String, ownerName: String, ownerAvatarUrl: String)

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    @Query("DELETE FROM posts WHERE id NOT IN (:ids)")
    suspend fun deleteAllNotIn(ids: List<String>)
}
