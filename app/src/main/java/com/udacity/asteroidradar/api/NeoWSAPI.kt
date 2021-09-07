package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface NeoWSAPIService {
    @GET("/neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("api_key") key: String
    ) : String

    @GET("/planetary/apod")
    suspend fun getPictureOfTheDay(
        @Query("api_key") key: String
    ) : NetworkPictureOfTheDay
}

object NeoWSAPI {
    val retrofitService : NeoWSAPIService by lazy {
        retrofit.create(NeoWSAPIService::class.java)
    }
}
