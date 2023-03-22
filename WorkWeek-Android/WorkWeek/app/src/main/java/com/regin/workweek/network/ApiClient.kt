package com.regin.workweek.network

import com.regin.workweek.utils.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


/*
class ApiClient {
    private lateinit var retrofit: Retrofit
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val client: Retrofit
        get() {

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit
        }
}*/

object ApiClient {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

interface ApiService {

    @GET("teams")
    fun fetchTeams(): Call<List<Team>>

    @GET("teams/{id}/persons")
    fun fetchPersons(@Path("id") teamId: Int): Call<List<User>>

    @GET("persons/{personId}/logs/weeks/{weekNumber}")
    fun fetchLogsForPerson(@Path("personId") personId: Int,
                           @Path("weekNumber") weekNumber: Int): Call<List<UserLog>>

    @GET("status")
    fun fetchStatus(): Call<List<Status>>

    @POST("batch/logs")
    fun addLog(@Body log: List<LogPost>): Call<List<LogPost>>
}