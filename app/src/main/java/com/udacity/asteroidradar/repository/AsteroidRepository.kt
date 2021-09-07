package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.data.AsteroidDatabaseDao
import com.udacity.asteroidradar.data.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfTheDay
import com.udacity.asteroidradar.main.SortType
import com.udacity.asteroidradar.utils.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.Exception

class AsteroidRepository( private val key: String, private val dao: AsteroidDatabaseDao) {
    val asteroids = MutableLiveData<List<Asteroid>?>()

    private val _neoNWSStatus = MutableLiveData<NetworkStatus>()
        val neoNWSStatus: LiveData<NetworkStatus>
            get() = _neoNWSStatus

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
            _neoNWSStatus.postValue(NetworkStatus.LOADING)
            try {
                val result = NeoWSAPI.retrofitService.getFeed(dao.today(), key)
                feed = parseAsteroidsJsonResult(JSONObject(result))
                dao.insertAll(*feed.asDatabaseModel())
                _neoNWSStatus.postValue(NetworkStatus.DONE)
            } catch (e: Exception) {
                Log.e("Asteroid Radar", "refreshAsteroids(): " + e.message.toString())
                feed = arrayListOf()
                _neoNWSStatus.postValue(NetworkStatus.ERROR)
            }
        }
    }

    fun getSortedAsteroidList(type: SortType): LiveData<List<Asteroid>?> {
        return Transformations.map(
            when (type) {
                SortType.WEEK -> dao.getWeeksAsteroids()
                SortType.SAVED ->  dao.getAllAsteroids()
                SortType.TODAY -> dao.getTodaysAsteroids()
            }
        ) {
            it?.asDomainModel()
        }
    }

    private val _potdStatus = MutableLiveData<NetworkStatus>()
    val potdStatus: LiveData<NetworkStatus>
        get() = _potdStatus

    private val _pictureOfTheDay = MutableLiveData<PictureOfTheDay?>()
    val pictureOfTheTheDay: LiveData<PictureOfTheDay?>
        get() = _pictureOfTheDay

    suspend fun refreshPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            _potdStatus.postValue(NetworkStatus.LOADING)
            try {
                _pictureOfTheDay.postValue(
                    NeoWSAPI.retrofitService.getPictureOfTheDay(key).asDomainModel()
                )
                _potdStatus.postValue(NetworkStatus.DONE)
            } catch (e: Exception) {
                Log.e("Asteroid Radar", "refreshPictureOfTheDay(): " + e.message.toString())
                _pictureOfTheDay.postValue(null)
                _potdStatus.postValue(NetworkStatus.ERROR)
            }
        }
    }

}