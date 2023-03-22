package com.regin.workweek.models

data class UserLogDataModel(
    val id: Int,
    val firstName: String = "",
    val lastName: String = "",
    val weekNumber: Int,
    val weekDayNumber: Int,
    var workplaceStatus: String? = "",
    var expanded: Boolean = false
) {

}