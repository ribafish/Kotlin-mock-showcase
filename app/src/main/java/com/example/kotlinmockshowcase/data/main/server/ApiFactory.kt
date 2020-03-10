package com.example.kotlinmockshowcase.data.main.server

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A factory that generates the reotrofit object used to connect to the mocky server
 */
object ApiFactory {

    private val logger = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        }
    }
    private val client = OkHttpClient().newBuilder().addInterceptor(logger).build()

    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("http://www.mocky.io/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val mockyApi : MockyApi = retrofit()
        .create(MockyApi::class.java)

}