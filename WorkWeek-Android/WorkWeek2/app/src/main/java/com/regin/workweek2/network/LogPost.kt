package com.regin.workweek2.network

import com.squareup.moshi.Json

data class LogPost (
    @Json(name = "personId")
    val personId: Int,
    @Json(name = "weekNumber")
    val weekNumber: Int,
    @Json(name = "weekDayNumber")
    val weekDayNumber: Int,
    @Json(name = "statusId")
    val statusId: Int
        )