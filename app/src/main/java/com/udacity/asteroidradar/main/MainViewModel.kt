package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.data.AsteroidDatabaseDao
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class SortType { WEEK, TODAY, SAVED }

class MainViewModel(key: String, dao: AsteroidDatabaseDao) : ViewModel() {
    private val repository = AsteroidRepository(key, dao)
    private val sortType = MutableLiveData<SortType>()

    val neoNWSStatus = repository.neoNWSStatus

    val potdStatus = repository.potdStatus
    val pictureOfTheDay = repository.pictureOfTheTheDay

    val asteroids = Transformations.switchMap(sortType) {
        repository.getSortedAsteroidList(it)
    }

    init {
        viewModelScope.launch {
            repository.refreshAsteroids()
            repository.refreshPictureOfTheDay()
        }
        sortType.value = SortType.WEEK
    }

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetail
        get() = _navigateToAsteroidDetail

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _navigateToAsteroidDetail.value = null
    }

    fun setSortType(type: SortType) {
        viewModelScope.launch {
            sortType.value = type
        }
    }

    fun retryRefreshAsteroidList() {
        viewModelScope.launch {
            repository.refreshAsteroids()
        }
    }

    fun retryRefreshPictureOfTheDay() {
        viewModelScope.launch {
            repository.refreshPictureOfTheDay()
        }
    }

}