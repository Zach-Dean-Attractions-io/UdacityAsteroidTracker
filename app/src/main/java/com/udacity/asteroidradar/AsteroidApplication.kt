package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.work.DeletePastDataWorker
import com.udacity.asteroidradar.work.RefreshAsteroidDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication: Application() {

    val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            setupAsteroidRefresh()
        }
    }

    private fun setupAsteroidRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) setRequiresDeviceIdle(true)
            }
            .build()

        val refreshAsteroidsRequest = PeriodicWorkRequestBuilder<RefreshAsteroidDataWorker>(1, TimeUnit.DAYS).setConstraints(constraints).build()
        val deletePastAsteroidsRequest = PeriodicWorkRequestBuilder<DeletePastDataWorker>(1, TimeUnit.DAYS).setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshAsteroidDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshAsteroidsRequest
        )

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            DeletePastDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            deletePastAsteroidsRequest
        )
    }
}