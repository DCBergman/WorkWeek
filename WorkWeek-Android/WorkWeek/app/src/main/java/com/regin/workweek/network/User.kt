package com.regin.workweek.network

import com.squareup.moshi.Json

data class User(
    @Json(name = "personId")
    val id: Int,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name="teamId")
    val teamId: Int
)