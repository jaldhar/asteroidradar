package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class AsteroidDataWorker(appContext: Context, params: WorkerParameters) :
CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRepository(applicationContext.getString(R.string.api_key),
            database.dao)
        return try {
            repository.refreshAsteroids()
            repository.deleteOldAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "AsteroidDataWorker"
    }
}
