package com.example.kotlinmockshowcase.data.main

import com.example.kotlinmockshowcase.data.main.server.MockyApi

/**
 * The repository that holds the posts data
 *
 * TODO: persist the posts
 */
class PostsRepository(private val api: MockyApi) {

    /**
     * Function used to get posts from mocky API.
     * Because MockyApy.getMockyPosts is suspendable, this needs to be as well to be able to be
     * called from a coroutine.
     */
    suspend fun getPosts() = api.getMockyPosts("59f2e79c2f0000ae29542931")
}