package com.danylom73.happyplaces.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "happy_places")
data class HappyPlaceModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val image: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)
