package ru.gb.dz18.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionsDao {
    @Query("SELECT*FROM attractions")
    fun getAll(): Flow<List<Attractions>>

    @Insert
    suspend fun insert(data: Attractions)

    @Query("DELETE FROM attractions")
    suspend fun delete()

    @Update
    suspend fun update(data: Attractions)
}