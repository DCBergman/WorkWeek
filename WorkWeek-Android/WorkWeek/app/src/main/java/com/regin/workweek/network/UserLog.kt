package com.regin.workweek.network

import com.squareup.moshi.Json

data class UserLog (
    @Json(name="logId")
    val logId: Int,
    @Json(name = "weekNumber")
    val weekNumber: Int,
    @Json(name = "weekDayNumber")
    val weekDayNumber: Int,
    @Json(name = "statusId")
    val statusId: Int,
    @Json(name = "statusCode")
    val statusCode: String,
    @Json(name = "teamId")
    val teamId: Int,
    @Json(name = "teamName")
    val teamName: String,
    @Json(name = "personId")
    val personId: Int,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,

    )