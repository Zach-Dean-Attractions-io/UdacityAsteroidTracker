package com.udacity.asteroidradar.api

import com.squareup.moshi.Json

class PictureOfTheDay (

    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String

) {
    val isImage
        get() = mediaType == "image"
}
