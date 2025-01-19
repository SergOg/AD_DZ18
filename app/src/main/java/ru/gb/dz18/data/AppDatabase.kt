package ru.gb.dz18.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Attractions::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attractionsDao(): AttractionsDao
}