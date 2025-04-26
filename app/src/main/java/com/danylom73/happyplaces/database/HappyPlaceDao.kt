package com.danylom73.happyplaces.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.danylom73.happyplaces.models.HappyPlaceModel

@Dao
interface HappyPlaceDao {
    @Insert
    suspend fun insertHappyPlace(result: HappyPlaceModel)

    @Query("SELECT * FROM happy_places ORDER BY id DESC")
    suspend fun getAllHappyPlaces(): List<HappyPlaceModel>

    @Query("SELECT * FROM happy_places WHERE id = :id")
    suspend fun getHappyPlaceById(id: Int): HappyPlaceModel

    @Query("DELETE FROM happy_places WHERE id = :id")
    suspend fun deleteHappyPlaceById(id: Int)
}