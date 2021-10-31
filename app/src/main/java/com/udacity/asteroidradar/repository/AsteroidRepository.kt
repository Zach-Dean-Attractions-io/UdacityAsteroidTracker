package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.PictureOfTheDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidsDatabase) {

    private val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroidsSince(dateFormat.format(currentTime))) { databaseAsteroids ->
        databaseAsteroids.asDomainModel()
    }

    suspend fun getPictureOfTheDay(): PictureOfTheDay? {
        return withContext(Dispatchers.IO) {
            try {
                AsteroidApi.retrofitService.getPictureOfTheDay(Constants.API_KEY)
            } catch(e: Exception) {
                println("*".repeat(50))
                println("Error Retrieving Picture Of The Day")
                println(e.message)
                println("*".repeat(50))
                null
            }

        }
    }

    suspend fun deleteAsteroidsPreviousTo(relevantDate: String) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deletePreviousTo(relevantDate)
        }
    }

    suspend fun retrieveAsteroidsBetween(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {

            try {
                val asteroidsResponse = AsteroidApi.retrofitService.getAsteroids(startDate, endDate, Constants.API_KEY)

                val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsResponse))

                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
            } catch(e: Exception) {
                println("*".repeat(50))
                println("Error Retrieving Asteroids")
                println(e.message)
                println("*".repeat(50))
            }


        }
    }
}