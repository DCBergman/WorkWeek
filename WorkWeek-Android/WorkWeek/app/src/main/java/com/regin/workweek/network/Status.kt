package com.regin.workweek.network

import com.squareup.moshi.Json

data class Status(
    @Json(name = "statusId")
    val statusId: Int,
    @Json(name = "code")
    val statusCode: String
)