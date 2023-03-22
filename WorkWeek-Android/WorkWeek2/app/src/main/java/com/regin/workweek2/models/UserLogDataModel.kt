package com.regin.workweek2.models

data class UserLogDataModel(
    val id: Int,
    val firstName: String = "",
    val lastName: String = "",
    var weekNumber: Int,
    var weekDayNumber: Int,
    var workplaceStatus: String? = "",
    var expanded: Boolean = false
) {

}