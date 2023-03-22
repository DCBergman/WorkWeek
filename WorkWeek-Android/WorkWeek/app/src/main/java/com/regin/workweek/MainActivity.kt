package com.regin.workweek

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek.models.UserLogDataModel
import com.regin.workweek.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

/*
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
*/

    private lateinit var rvWorkStatus: RecyclerView
    private lateinit var rvLandscape: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var landscapeAdapter: LandscapeAdapter
    private var userLogs: MutableList<UserLogDataModel> = arrayListOf()
    private lateinit var service: ApiService
    private var supportFuncs: SupportFuncs = SupportFuncs()
    private lateinit var displayDate: LocalDate
    private var weeklyLogs: MutableList<Pair<Int?, List<UserLog>?>> = mutableListOf()
    private var options: List<Status> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = supportFuncs.getService()

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)

        val actionBarView: View = supportActionBar!!.customView
        val forwardBtn = actionBarView.findViewById<ImageView>(R.id.ivForwardArrow)
        val backBtn = actionBarView.findViewById<ImageView>(R.id.ivBackArrow)
        val llWeekDate = actionBarView.findViewById<LinearLayout>(R.id.llWeekDate)

      /*  viewModel.teamMembersLiveData.observe(this){
            Log.d("MainActivity", "Users from vm: ${it.size}")
        }*/

        val dt: LocalDate = LocalDate.now()
        displayDate = dt

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            var trHeader = findViewById<TableRow>(R.id.trHeader)
            trHeader.setBackgroundColor(Color.parseColor("#A7CBAB"))
        }

        supportFuncs.setDisplayDate(displayDate, actionBarView)

        getStatusOptions()
        getTeamMembers(1, supportFuncs.getDisplayWeekFromDate(displayDate))

        forwardBtn.setOnClickListener {

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                getAndDisplayPortraitLogs(1, actionBarView)
                createPortraitRvAdapter()

            } else {
                getAndDisplayLandscapeLogs(7, actionBarView)
                createLandscapeRvAdapter()
            }

        }

        backBtn.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
               getAndDisplayPortraitLogs(-1, actionBarView)
                createPortraitRvAdapter()
            } else {
               getAndDisplayLandscapeLogs(-7, actionBarView)
                createLandscapeRvAdapter()
            }

        }
        llWeekDate.setOnClickListener {
            displayDate = LocalDate.now()
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                getAndDisplayPortraitLogs(0, actionBarView)
                createPortraitRvAdapter()
            } else {
                getAndDisplayLandscapeLogs(0, actionBarView)
                createLandscapeRvAdapter()
            }

        }

    }

    private fun getAndDisplayPortraitLogs(increment: Int, view: View) {

        rvWorkStatus = findViewById(R.id.rvWorkStatus)
        displayDate = supportFuncs.getNextDate(displayDate, increment)
        val weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)
        getTeamMembers(1, weekNumber)
        setDisplayDate(displayDate, view)
    }

    private fun getAndDisplayLandscapeLogs(increment: Int, view: View) {

        displayDate = supportFuncs.getNextDate(displayDate, increment)

        val weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)

            getTeamMembers(1, weekNumber)
        setDisplayDate(displayDate, view)
    }


    private fun setDisplayDate(displayDate: LocalDate, view: View) {

        val weekday = supportFuncs.getSweWeekDay(displayDate.dayOfWeek.value)
        val dayOfMonth = displayDate.dayOfMonth.toString()
        val month = displayDate.monthValue
        val weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)

        val dateString = "$weekday $dayOfMonth/$month"

        val abtv = view.findViewById<TextView>(R.id.tvWeek)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        abtv.text = "Vecka $weekNumber"

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvDate.text = dateString
        }else{
            var startDate = displayDate.with(DayOfWeek.MONDAY).toString()
            var endDate = displayDate.with(DayOfWeek.SUNDAY).toString()

            tvDate.text = "$startDate   -   $endDate"
        }


    }

    private fun getStatusOptions() {
        service.fetchStatus().enqueue(object : Callback<List<Status>> {
            override fun onResponse(call: Call<List<Status>>, response: Response<List<Status>>) {
                if (response.body() != null) {

                    options = response.body()!!
                }
            }

            override fun onFailure(call: Call<List<Status>>, t: Throwable) {
                Log.d("Support Funcs", "Could not get log status.")
            }

        })
    }

    private fun getTeamMembers(teamId: Int, weekNumber: Int) {
        userLogs.clear()
        weeklyLogs.clear()
        service.fetchPersons(teamId).enqueue(object : Callback<List<User>> {

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("MainActivity", "Users error: ${t.message}")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.body() != null) {
                    for (r in response.body()!!.toList()) {
                        userLogs.add(
                            UserLogDataModel(
                                r.id,
                                r.firstName,
                                r.lastName,
                                weekNumber,
                                displayDate.dayOfWeek.value,
                                "",
                                false
                            )
                        )
                        getLogsForPersonPerWeek(r.id, weekNumber)

                    }
                } else {
                    Log.d("MainActivity", "Users error: could not get team members")
                }
            }
        })
    }


    private fun getLogsForPersonPerWeek(userId: Int, weekNumber: Int) {
        Log.d("MainActivity", "User före: $userLogs")
        service.fetchLogsForPerson(userId, weekNumber).enqueue(object : Callback<List<UserLog>> {

            override fun onFailure(call: Call<List<UserLog>>, t: Throwable) {
                Log.d("MainActivity", "Log error: ${t.message}")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<UserLog>>, response: Response<List<UserLog>>) {

                if (response.body() != null) {
                    for (r in response.body()!!.toList()) {

                        userLogs.find { it.id == r.personId && it.weekNumber == weekNumber && it.weekDayNumber == r.weekDayNumber }?.workplaceStatus =
                            r.statusCode
                    }

                }
                var id = userId
                var pair = Pair(id, response.body())
                weeklyLogs.add(pair)


                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    createPortraitRvAdapter()
                } else {
                    createLandscapeRvAdapter()
                }


            }

        })


    }

    private fun createPortraitRvAdapter() {
//        Log.d("MainActivity", "Weekly: ${weeklyLogs.last()}")
        rvWorkStatus = findViewById(R.id.rvWorkStatus)


        mainAdapter = MainAdapter(
            this@MainActivity,
            userLogs,
            // dailyLogs,
            weeklyLogs,
            options,
            displayDate
        )

        rvWorkStatus.adapter = mainAdapter
        rvWorkStatus.layoutManager = LinearLayoutManager(this@MainActivity)
        rvWorkStatus.setHasFixedSize(true)
    }

    private fun createLandscapeRvAdapter() {


        rvLandscape = findViewById(R.id.rvLandscapeTable)

        landscapeAdapter = LandscapeAdapter(
            userLogs,
            weeklyLogs,
            this@MainActivity
        )

        rvLandscape.adapter = landscapeAdapter
        rvLandscape.layoutManager = LinearLayoutManager(this@MainActivity)
        rvLandscape.setHasFixedSize(true)


    }

}