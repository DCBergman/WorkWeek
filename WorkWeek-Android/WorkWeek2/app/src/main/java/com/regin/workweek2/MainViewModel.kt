package com.regin.workweek2

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.regin.workweek.repositories.Repository
import com.regin.workweek2.models.UserLogDataModel
import com.regin.workweek2.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import kotlin.properties.Delegates

class MainViewModel(
    private val repository: Repository
    = Repository(ApiClient.apiService)
) : ViewModel() {

    private lateinit var displayDate: LocalDate
    private var weekNumber by Delegates.notNull<Int>()
    private lateinit var supportFuncs: SupportFuncs
    var numberOfTeamMembers: Int = 0;
    var totalLogs: MutableList<UserLog> = mutableListOf<UserLog>()

    private var _teamMembersLiveData = MutableLiveData<List<User>>()
    val teamMembersLiveData: LiveData<List<User>>
        get() = _teamMembersLiveData

    private var _logsLiveData = MutableLiveData<ScreenState<List<UserLog>?>>()
    val logsLiveData: LiveData<ScreenState<List<UserLog>?>>
        get() = _logsLiveData

    private var _statusLiveData = MutableLiveData<List<Status>>()
    val statusLiveData: LiveData<List<Status>>
        get() = _statusLiveData

    private var _postLogsLiveData = MutableLiveData<List<LogPost>>()
    val postLogsLiveData: LiveData<List<LogPost>>
        get() = _postLogsLiveData


    init {
        setData()
        fetchTeamMembers(1)
        fetchStatusOptions()
    }
    private fun setData(){
        supportFuncs = SupportFuncs()
        displayDate = LocalDate.now()
        weekNumber = supportFuncs.getDisplayWeekFromDate(displayDate)

    }

    fun fetchTeamMembers(teamId: Int) {

        val client = repository.getTeamMembers(teamId)
        client.enqueue(object : Callback<List<User>> {

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("MainActivity", "Users error: ${t.message}")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    _teamMembersLiveData.postValue(response.body())
                } else {
                    Log.d("MainActivity", "Users error: could not get team members")
                }
            }
        })
    }


    fun fetchLogsForPersonPerWeek(id: Int, weekNumber: Int) {

        val client = repository.getLogs(id, weekNumber)
//        _logsLiveData.postValue(ScreenState.Loading(null))
         client.enqueue(object : Callback<List<UserLog>> {
            override fun onResponse(
                call: Call<List<UserLog>>, response: Response<List<UserLog>>
            ) {

                if (response.isSuccessful) {
                    numberOfTeamMembers++
                    Log.d("FetchLogs", response.body().toString())
                    response.body()?.let { totalLogs.addAll(it) }
                    if(numberOfTeamMembers == 9){
                        _logsLiveData.postValue(ScreenState.Success(totalLogs))

//                        _logsLiveData.postValue(ScreenState.Success(response.body()))
                    }
                } else {
                    _logsLiveData.postValue(
                        ScreenState.Error(
                            response.code().toString(),
                            null
                        )
                    )
                }
            }
            override fun onFailure(call: Call<List<UserLog>>, t: Throwable) {
                Log.d("Failure", t.message.toString())
                _logsLiveData.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })

    }
    private fun fetchStatusOptions() {

        val client = repository.getStatusOptions()
        client.enqueue(object : Callback<List<Status>> {
            override fun onResponse(
                call: Call<List<Status>>, response: Response<List<Status>>
            ) {
                if (response.isSuccessful) {
                    _statusLiveData.postValue(response.body())
                } else {
                    Log.d("MainActivity", "Users error: could not get status options")

                }
            }

            override fun onFailure(call: Call<List<Status>>, t: Throwable) {
                Log.d("Failure", t.message.toString())
                t.printStackTrace()
            }

        })
    }

    fun postLogs(logs: List<LogPost>){

        val client = repository.postLogs(logs)
        client.enqueue(object : Callback<List<LogPost>> {
            override fun onResponse(call: Call<List<LogPost>>, response: Response<List<LogPost>>) {
                Log.d("SupportFuncs", "Post was successful! response: $response")

                if (response.isSuccessful) {
                    _postLogsLiveData.postValue(response.body())
                } else {
                    Log.d("MainActivity", "Could not post user logs")

                }
            }

            override fun onFailure(call: Call<List<LogPost>>, t: Throwable) {
                Log.d("SupportFuncs", "An error has occured: ${t.message}")
            }

        })
    }



}

