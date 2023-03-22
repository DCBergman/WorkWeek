package com.regin.workweek2.models

data class NestedListModel(val weekDays: List<String> =listOf("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag"),
val options: List<String> = listOf("", "Hemma", "Lund", "Sjuk", "Semester", "Övrigt")) {
}