package com.regin.workweek2

import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek2.models.UserLogDataModel
import com.regin.workweek2.network.ApiClient
import com.regin.workweek2.network.ApiService
import com.regin.workweek2.network.LogPost
import com.regin.workweek2.network.UserLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields

class SupportFuncs() {


    private lateinit var rvWorkStatus: RecyclerView
    private lateinit var rvLandscape: RecyclerView
    private lateinit var displayDate: LocalDate
    private var weeklyLogs: MutableList<Pair<Int?, List<UserLog>?>> = mutableListOf()
    private lateinit var service: ApiService
    var userLogs: MutableList<UserLogDataModel> = arrayListOf()

    fun getNextDate(date: LocalDate, increment: Int): LocalDate {

        return date.plusDays(increment.toLong())
    }

    fun getSweWeekDay( weekday: Int): String{
        var sweWeekdays: List<String> = listOf("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag")
        var weekdayString = sweWeekdays[weekday-1]

        return weekdayString
    }

    fun setDisplayDate(displayDate: LocalDate, view: View) {

        val weekday = getSweWeekDay(displayDate.dayOfWeek.value)
        val dayOfMonth = displayDate.dayOfMonth.toString()
        val month = displayDate.monthValue
        val weekNumber = getDisplayWeekFromDate(displayDate)

        val dateString = "$weekday $dayOfMonth/$month"

        val abtv = view.findViewById<TextView>(R.id.tvWeek)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        abtv.text = "Vecka $weekNumber"

        if (view.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvDate.text = dateString
        }else{
            var startDate = displayDate.with(DayOfWeek.MONDAY).toString()
            var endDate = displayDate.with(DayOfWeek.SUNDAY).toString()

            tvDate.text = "$startDate   -   $endDate"
        }


    }
    fun getDisplayWeekFromDate(displayDate: LocalDate): Int {
        val wf: WeekFields = WeekFields.of(DayOfWeek.of(1), 4)
        return displayDate.get(wf.weekOfWeekBasedYear())
    }

    fun getService(): ApiService{
        return ApiClient.apiService
    }

    fun postLog(logs: List<LogPost>){
        var service = getService()

        service.addLog(logs).enqueue(object : Callback<List<LogPost>> {
            override fun onResponse(call: Call<List<LogPost>>, response: Response<List<LogPost>>) {
                Log.d("SupportFuncs", "Post was successful! response: $response")
            }

            override fun onFailure(call: Call<List<LogPost>>, t: Throwable) {
                Log.d("SupportFuncs", "An error has occured: ${t.message}")
            }

        })
    }


}