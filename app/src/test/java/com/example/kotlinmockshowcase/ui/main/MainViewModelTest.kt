package com.example.kotlinmockshowcase.ui.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.kotlinmockshowcase.data.main.PostsRepository
import com.example.kotlinmockshowcase.data.main.response.MockyPost
import com.example.kotlinmockshowcase.data.main.response.MockyResponse
import com.example.kotlinmockshowcase.data.main.server.MockyApi
import com.example.kotlinmockshowcase.general.CoroutineContextProvider
import com.example.kotlinmockshowcase.general.NetworkResult
import com.example.kotlinmockshowcase.util.MainCoroutineRule
import com.example.kotlinmockshowcase.util.getOrAwaitValue
import com.example.kotlinmockshowcase.util.observeForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


/**
 * Unit tests for [MainViewModel].
 */
@ExperimentalCoroutinesApi
open class MainViewModelTest {

    // Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

//     Sets the main coroutines dispatcher to a TestCoroutineScope for unit testing.
    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val mainCoroutineRule = MainCoroutineRule()

    // Use a Fake DataSource so we have all necessary control over it
    private val fakeMockyApi = FakeMockyApi()
    private val contextProvider = TestCoroutineContextProvider()

    private val viewModel = MainViewModel(PostsRepository(fakeMockyApi), contextProvider)

    @Test
    fun testPosts() {
        println("testPosts")
        assertFalse(viewModel.mockyPosts.hasObservers())

        val posts = ArrayList<MockyPost>()
        val mockyResponse = MockyResponse(posts)
        posts.add(MockyPost(1, 1, "", "", "", ""))

        assertTrue("result is ${viewModel.networkResult.value}", viewModel.networkResult.value is NetworkResult.Loading)

        assertFalse(viewModel.mockyPosts.hasObservers())
        assertFalse(viewModel.networkResult.hasObservers())

        viewModel.networkResult.observeForever{}
        viewModel.mockyPosts.observeForever{}

        assertTrue(viewModel.mockyPosts.hasObservers())
        assertTrue(viewModel.networkResult.hasObservers())

        viewModel.refreshPosts()

        assertTrue("result is ${viewModel.networkResult.value}", viewModel.networkResult.value is NetworkResult.Error)

        fakeMockyApi.createSuccessResponse(mockyResponse)

        viewModel.refreshPosts()

        assertTrue("result is ${viewModel.networkResult.value}", viewModel.networkResult.value is NetworkResult.Success)
        assertTrue("result is ${viewModel.mockyPosts.value}",
            viewModel.mockyPosts.getOrAwaitValue() == posts
        )


    }
}


@ExperimentalCoroutinesApi
class FakeMockyApi : MockyApi {

    private lateinit var response : Response<MockyResponse>

    init{
        createErrorResponse(999)
    }

    override suspend fun getMockyPosts(mockyId: String): Response<MockyResponse> {
//        println("getMockyPosts")
        return response
    }

    fun createErrorResponse(code : Int) {
        val body = ResponseBody.Companion.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{\"error\":\"mock_error\",\"error_description\":\"Mock error\",\"code\":400}")
        response = Response.error(code, body)
    }

    fun createSuccessResponse(data : MockyResponse) {
//        println("createSuccessResponse")
        response = Response.success(data)
    }

}

/**
 * This will change all dispatchers to Unconfined, which executes initial continuation
 * of the coroutine in the current call-frame and lets the coroutine resume in whatever
 * thread that is used by the corresponding suspending function,
 * without mandating any specific threading policy.
 */
class TestCoroutineContextProvider : CoroutineContextProvider() {
    override val Main: CoroutineContext = Dispatchers.Unconfined
    override val IO: CoroutineContext = Dispatchers.Unconfined
}