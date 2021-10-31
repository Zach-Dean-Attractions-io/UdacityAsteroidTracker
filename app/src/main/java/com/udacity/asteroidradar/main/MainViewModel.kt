package com.udacity.asteroidradar.main

import android.app.Application
import android.app.PictureInPictureParams
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.PictureOfTheDay
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

class MainViewModel(application: Application): AndroidViewModel(application) {

    /** Repository **/
    private val database = getDatabase(application)

    private val asteroidsRepository = AsteroidRepository(database)

    /** Picture Of The Day **/
    private val _pictureOfTheDay = MutableLiveData<PictureOfTheDay>()

    val pictureOfTheDay: LiveData<PictureOfTheDay>
        get() = _pictureOfTheDay

    /** Asteroids **/
    val asteroids = asteroidsRepository.asteroids

    /** Factory **/
    class Factory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable To Create ViewModel")
        }
    }

    /** Init **/
    init {
        viewModelScope.launch {
            asteroidsRepository.retrieveAsteroidsBetween(
                getNextSevenDaysFormattedDates()[0],
                getNextSevenDaysFormattedDates()[Constants.DEFAULT_END_DATE_DAYS - 1]
            )
        }

        viewModelScope.launch {
            _pictureOfTheDay.value = asteroidsRepository.getPictureOfTheDay()
        }

    }



    /** Navigation **/
    private val _navigateToDetail = MutableLiveData<Asteroid>()

    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    fun navigateToAsteroidDetails(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onNavigationToAsteroidDetailsComplete() {
        _navigateToDetail.value = null
    }

}