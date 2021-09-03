package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.data.AsteroidDatabaseDao
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class NeoWSAPIStatus { LOADING, ERROR, DONE }

class MainViewModel(key: String, dao: AsteroidDatabaseDao) : ViewModel() {
    private val repository = AsteroidRepository(key, dao)

    val status = repository.status

    val feed = repository.asteroids

    init {
        viewModelScope.launch {
            repository.refreshAsteroids()
        }
    }
}