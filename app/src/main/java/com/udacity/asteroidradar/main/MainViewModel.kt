package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NeoWSAPI
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

enum class NeoWSAPIStatus { LOADING, ERROR, DONE }

class MainViewModel(key: String) : ViewModel() {
    private val _status = MutableLiveData<NeoWSAPIStatus>()
    val status: LiveData<NeoWSAPIStatus>
        get() = _status

    private val _feed = MutableLiveData<List<Asteroid>>()
    val feed: LiveData<List<Asteroid>>
        get() = _feed

    init {
        getFeed(key)
    }

    private fun getFeed(key: String) {
        viewModelScope.launch {
            _status.value = NeoWSAPIStatus.LOADING
            try {
                val result = JSONObject(NeoWSAPI.retrofitService.getFeed(key))
                _feed.value = parseAsteroidsJsonResult(result)
                _status.value = NeoWSAPIStatus.DONE
            } catch (e: Exception) {
                Log.e("Asteroid Radar", e.message.toString())
                _feed.value = listOf()
                _status.value = NeoWSAPIStatus.ERROR
            }
        }
    }
}