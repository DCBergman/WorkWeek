
package com.regin.workweek.repositories

import com.regin.workweek2.network.ApiClient
import com.regin.workweek2.network.ApiService
import com.regin.workweek2.network.LogPost

class Repository(private val apiService: ApiService) {
    fun getTeamMembers(teamId: Int) = apiService.fetchPersons(teamId)
    fun getLogs(id:Int, weekNumber:Int) = apiService.fetchLogsForPerson(id, weekNumber)
    fun getStatusOptions() = apiService.fetchStatus()
    fun postLogs(logs: List<LogPost>) = apiService.addLog(logs)
}
