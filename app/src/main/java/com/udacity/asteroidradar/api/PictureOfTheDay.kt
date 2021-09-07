package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.udacity.asteroidradar.domain.PictureOfTheDay

data class NetworkPictureOfTheDay(
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String
)

fun NetworkPictureOfTheDay.asDomainModel() : PictureOfTheDay {
    return PictureOfTheDay(mediaType, title, url)
}