package com.example.kotlinmockshowcase.data.main.response

import com.google.gson.annotations.SerializedName

/**
 * Descripttion of the response the mocky RESTful API returns
 */
data class MockyResponse (
    @SerializedName("posts")
    val mockyPosts: List<MockyPost>
)

/**
 * Decription of a single post the mocky RESTful API returns
 */
data class MockyPost (
    val id: Int,
    val user_id: Int,
    val title: String,
    val description: String,
    @SerializedName("image")
    val imageUrl: String,
    val published_at: String
)