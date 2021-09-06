package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.data.AsteroidDatabaseDao
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class NeoWSAPIStatus { LOADING, ERROR, DONE }
enum class SortType { WEEK, TODAY, SAVED }

class MainViewModel(key: String, dao: AsteroidDatabaseDao) : ViewModel() {
    private val repository = AsteroidRepository(key, dao)
    private val sortType = MutableLiveData<SortType>()

    val status = repository.status

    val asteroids = Transformations.switchMap(sortType) {
        repository.getSortedAsteroidList(it)
    }

    init {
        viewModelScope.launch {
            repository.refreshAsteroids()
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
}