package com.example.cityconnect.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cityconnect.model.dao.PostDao
import com.example.cityconnect.model.dao.UserDao
import com.example.cityconnect.model.dao.PlaceDao
import com.example.cityconnect.model.schemas.PostEntity
import com.example.cityconnect.model.schemas.UserEntity
import com.example.cityconnect.model.schemas.PlaceEntity

@Database(
    entities = [UserEntity::class, PostEntity::class, PlaceEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cityconnect.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
