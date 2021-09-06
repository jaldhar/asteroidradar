package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDatabaseDao {
    @Query("DELETE FROM asteroids WHERE close_approach_date < date('now')")
    fun deleteOldAsteroids()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("SELECT date('now')")
    fun today() : String

    @Query("SELECT * FROM asteroids WHERE close_approach_date BETWEEN date('now') AND " +
            "date('now', '+7 days') ORDER BY close_approach_date ASC")
    fun getWeeksAsteroids() : LiveData<List<DatabaseAsteroid>?>

    @Query("SELECT * FROM asteroids WHERE close_approach_date = date('now')" +
            " ORDER BY close_approach_date ASC")
    fun getTodaysAsteroids() : LiveData<List<DatabaseAsteroid>?>

    @Query("SELECT * FROM asteroids ORDER BY close_approach_date ASC")
    fun getAllAsteroids() : LiveData<List<DatabaseAsteroid>?>

}