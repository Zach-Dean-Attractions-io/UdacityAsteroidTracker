package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class RefreshAsteroidDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshAsteroidsWorker"
    }

    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        val currentCalendar = Calendar.getInstance()
        val currentTime = currentCalendar.time

        currentCalendar.add(Calendar.DAY_OF_YEAR, 6)
        val weeksTime = currentCalendar.time

        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        return try {
            repository.retrieveAsteroidsBetween(dateFormat.format(currentTime), dateFormat.format(weeksTime))
            Result.success()
        } catch(e: HttpException) {
            Result.retry()
        }

    }

}

class DeletePastDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "DeletePastDataWorker"
    }

    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        return try {
            repository.deleteAsteroidsPreviousTo(dateFormat.format(currentTime))
            Result.success()
        } catch(e: HttpException) {
            Result.retry()
        }

    }

}