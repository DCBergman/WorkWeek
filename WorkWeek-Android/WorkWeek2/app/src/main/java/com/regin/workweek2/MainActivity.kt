package com.regin.workweek2

import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek2.models.UserLogDataModel
import com.regin.workweek2.network.*
import java.time.DayOfWeek
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private var userLogs: MutableList<UserLogDataModel> = arrayListOf()
    private var weeklyLogs: MutableList<Pair<Int?, List<UserLog>?>> = mutableListOf()
    private lateinit var loading: RelativeLayout
    private lateinit var rvWorkStatus: RecyclerView
    private lateinit var rvLandscape: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var landscapeAdapter: LandscapeAdapter
    private lateinit var displayDate: LocalDate
    private lateinit var options: List<Status>
    private lateinit var actionBarView: View
    private var supportFuncs: SupportFuncs = SupportFuncs()
    private var weekNumber: Int = 0

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)

        actionBarView = supportActionBar!!.customView
        val forwardBtn = actionBarView.findViewById<ImageView>(R.id.ivForwardArrow)
        val backBtn = actionBarView.findViewById<ImageView>(R.id.ivBackArrow)
        val doubleForwardBtn = actionBarView.findViewById<ImageView>(R.id.ivDoubleForwardArrow)
        val doubleBackwardBtn = actionBarView.findViewById<ImageView>(R.id.ivDoubleBackArrow)

        val dt: LocalDate = LocalDate.now()
        Log.d("MainActivity", "LocalDate: $dt")
        displayDate = dt
        weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            var trHeader = findViewById<TableRow>(R.id.trHeader)
            trHeader.setBackgroundColor(Color.parseColor("#A7CBAB"))
        }

        supportFuncs.setDisplayDate(displayDate, actionBarView)

        viewModel.teamMembersLiveData.observe(this) { state ->
            processTeamMembersResponse(state)
        }

        viewModel.logsLiveData.observe(this) { state ->
            processUserLogsResponse(state)
        }

        viewModel.statusLiveData.observe(this) { state ->
            processStatusResponse(state)
        }

        viewModel.postLogsLiveData.observe(this) { state ->
            processPostLogsResponse(state)
        }

        forwardBtn.setOnClickListener {

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                updateAndDisplayLogs(1, actionBarView)
            } else {
                updateAndDisplayLogs(7, actionBarView)
            }

        }

        backBtn.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                updateAndDisplayLogs(-1, actionBarView)
            } else {
                updateAndDisplayLogs(-7, actionBarView)
            }

        }

        doubleForwardBtn.setOnClickListener {
            updateAndDisplayLogs(7, actionBarView)
        }

        doubleBackwardBtn.setOnClickListener {
            updateAndDisplayLogs(-7, actionBarView)
        }
    }

    private fun updateAndDisplayLogs(increment: Int, actionBarView: View) {
        viewModel.totalLogs.clear()

        displayDate = supportFuncs.getNextDate(displayDate, increment)
        weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)
        setDisplayDate(displayDate, actionBarView)


        viewModel.numberOfTeamMembers = 0;
        for (t in userLogs) {
            t.weekNumber = weekNumber
            t.weekDayNumber = displayDate.dayOfWeek.value
            t.workplaceStatus = ""

            viewModel.fetchLogsForPersonPerWeek(
                t.id,
                weekNumber
            )
        }
    }


    private fun setDisplayDate(displayDate: LocalDate, view: View) {

        val weekday = supportFuncs.getSweWeekDay(displayDate.dayOfWeek.value)
        val dayOfMonth = displayDate.dayOfMonth.toString()
        val month = displayDate.monthValue

        val dateString = "$weekday $dayOfMonth/$month"

        val abtv = view.findViewById<TextView>(R.id.tvWeek)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        abtv.text = "Vecka $weekNumber"

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvDate.text = dateString
        } else {
            var startDate = displayDate.with(DayOfWeek.MONDAY).toString()
            var endDate = displayDate.with(DayOfWeek.SUNDAY).toString()

            tvDate.text = "$startDate   -   $endDate"
        }


    }

    private fun processStatusResponse(status: List<Status>) {
        options = status
    }

    private fun processTeamMembersResponse(teamMembers: List<User>) {
        userLogs.clear()
        weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)

        if (!teamMembers.isNullOrEmpty()) {

            for (s in teamMembers.toList()) {
                userLogs.add(
                    UserLogDataModel(
                        s.id,
                        s.firstName,
                        s.lastName,
                        weekNumber,
                        displayDate.dayOfWeek.value,
                        "",
                        false
                    )
                )
            }

            viewModel.numberOfTeamMembers = 0;

            for (t in userLogs) {
                viewModel.fetchLogsForPersonPerWeek(
                    t.id,
                    weekNumber
                )
            }
        }

    }

    private fun processPostLogsResponse(logPosts: List<LogPost>?) {
        updateAndDisplayLogs(0, actionBarView)
    }

    private fun processUserLogsResponse(state: ScreenState<List<UserLog>?>) {
        loading = findViewById(R.id.loadingPanel)
//        Log.d("MainActivity", "Log state: $state")
        weeklyLogs.clear()
        when (state) {
            is ScreenState.Loading -> {
                Log.d("MainActivity", "Loading...")
                loading.isVisible = true
            }
            is ScreenState.Success -> {
                if (!state.data.isNullOrEmpty()) {
                    /* Log.d("MainActivity", "Log data: ${state.data}")*/
                    for (r in state.data) {

                        userLogs.find { it.id == r.personId && it.weekNumber == weekNumber && it.weekDayNumber == r.weekDayNumber }?.workplaceStatus =
                            r.statusCode ?: ""
                    }
                    for (u in userLogs) {
                        var logs = state.data.filter { it.personId == u.id }
                        weeklyLogs.add(Pair(u.id, logs))
                    }
                    loading.isVisible = false
                    Log.d("WeeklyLogs", weeklyLogs.toString())

                } else {
                    weeklyLogs.add(Pair(userLogs[0].id, emptyList()))
                    loading.isVisible = false
                }
            }
            is ScreenState.Error -> {
                Log.d("MainActivity", "Error!")
            }
        }
        if (viewModel.numberOfTeamMembers == userLogs.size) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                createPortraitRvAdapter()
            } else {
                createLandscapeRvAdapter()
            }
        }
    }

    private fun createPortraitRvAdapter() {

        mainAdapter = MainAdapter(
            this@MainActivity,
            userLogs,
            weeklyLogs,
            options,
            displayDate,
            viewModel
        )

        rvWorkStatus = findViewById(R.id.rvWorkStatus)

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