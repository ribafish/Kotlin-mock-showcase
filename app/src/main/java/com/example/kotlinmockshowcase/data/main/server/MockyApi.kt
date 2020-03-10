package com.example.kotlinmockshowcase.data.main.server

import com.example.kotlinmockshowcase.data.main.response.MockyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * The Mocky API interfacte that describes the http actions available
 */
interface MockyApi {

    /**
     * Function used to get posts from mocky API.
     *
     * Retrofit2 v2.6.0+ has native support for Kotlin coroutines, which this function leverages.
     */
    @GET("{id}")
    suspend fun getMockyPosts(@Path(value = "id") mockyId: String) : Response<MockyResponse>
}