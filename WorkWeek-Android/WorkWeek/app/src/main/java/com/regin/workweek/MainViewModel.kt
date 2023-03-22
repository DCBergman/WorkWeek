/*
package com.regin.workweek

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.regin.workweek.models.UserLogDataModel
import com.regin.workweek.network.*
import com.regin.workweek.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    private val repository: Repository
    = Repository(ApiClient.apiService)
) : ViewModel() {

    private var userLogs: MutableList<UserLogDataModel> = arrayListOf()
    private var weeklyLogs: MutableList<Pair<Int?, List<UserLog>?>> = mutableListOf()
    private var options: List<Status> = listOf()

    private var _teamMembersLiveData = MutableLiveData<List<User>>()
    val teamMembersLiveData: LiveData<List<User>>
        get() = _teamMembersLiveData

    private var _logsLiveData = MutableLiveData<List<Log>?>()
    val characterLiveData: LiveData<List<Log>?>
        get() = _logsLiveData

    init {
        fetchTeamMembers(1)
//        fetchLogs()
    }

    private fun fetchTeamMembers(teamId: Int) {
        */
/*userLogs.clear()
        weeklyLogs.clear()*//*

        val client = repository.getTeamMembers(teamId)
        client.enqueue(object : Callback<List<User>> {

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("MainActivity", "Users error: ${t.message}")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    _teamMembersLiveData.postValue(response.body())
                }else{
                    Log.d("MainActivity", "Users error: could not get team members")
                }
            }
        })
    }

    */
/* private fun fetchLogs(id:Int, weekNumber:Int){

         val client = repository.getLogs(id, weekNumber)
         _logsLiveData.postValue(ScreenState.Loading(null))
         client.enqueue(object : Callback<>{
             override fun onResponse(
                 call: Call<CharacterResponse>,
                 response: Response<CharacterResponse>
             ) {
                 if(response.isSuccessful){
                     _charactersLiveData.postValue(ScreenState.Success(response.body()?.result))
                 }else{
                     _charactersLiveData.postValue(ScreenState.Error(response.code().toString(), null))
                 }
             }

             override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                 Log.d("Failure", t.message.toString())
                 _charactersLiveData.postValue(ScreenState.Error(t.message.toString(), null))
             }

         })

     }*//*

}

*/
