package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.BASE_URL
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NeoWSAPIService {
    @GET("/neo/rest/v1/feed")
    suspend fun getFeed(@Query("api_key")key: String) : String
}

object NeoWSAPI {
    val retrofitService : NeoWSAPIService by lazy {
        retrofit.create(NeoWSAPIService::class.java)
    }
}