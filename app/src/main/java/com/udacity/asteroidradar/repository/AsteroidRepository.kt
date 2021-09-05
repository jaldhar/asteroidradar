package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.NeoWSAPI
import com.udacity.asteroidradar.api.NetworkAsteroid
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.data.AsteroidDatabaseDao
import com.udacity.asteroidradar.data.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.main.NeoWSAPIStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.Exception

class AsteroidRepository( private val key: String, private val dao: AsteroidDatabaseDao) {
    val asteroids : LiveData<List<Asteroid>> =
        Transformations.map(dao.getAllAsteroids()) {
            it?.asDomainModel()
        }

    private val _status = MutableLiveData<NeoWSAPIStatus>()
        val status: LiveData<NeoWSAPIStatus>
            get() = _status

    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                dao.deleteOldAsteroids()
            } catch (e: Exception) {
                Log.e("Asteroid Radar", e.message.toString())
            }
        }
    }

    suspend fun refreshAsteroids() {
        var feed : ArrayList<NetworkAsteroid>
        withContext(Dispatchers.IO) {
            _status.postValue(NeoWSAPIStatus.LOADING)
            try {
                val result = NeoWSAPI.retrofitService.getFeed(key)
                feed = parseAsteroidsJsonResult(JSONObject(result))
                dao.insertAll(*feed.asDatabaseModel())
                _status.postValue(NeoWSAPIStatus.DONE)
            } catch (e: Exception) {
                Log.e("Asteroid Radar", e.message.toString())
                feed = arrayListOf()
                _status.postValue(NeoWSAPIStatus.ERROR)
            }
        }
    }
}