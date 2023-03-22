package com.regin.workweek2.network

import com.squareup.moshi.Json

data class Team(
    @Json(name = "teamId")
    val id: Int,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "description")
    val description: String? = null

)

/*data class TeamResponse(
//    @Json(name = "results")
    val result: List<Team>
)*/

