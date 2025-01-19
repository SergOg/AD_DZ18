package ru.gb.dz18.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
data class Attractions(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "uri") var uri: String
)