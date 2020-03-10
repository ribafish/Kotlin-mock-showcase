package com.example.kotlinmockshowcase.ui.main

import androidx.lifecycle.*
import com.example.kotlinmockshowcase.data.main.PostsRepository
import com.example.kotlinmockshowcase.data.main.response.MockyPost
import com.example.kotlinmockshowcase.data.main.response.MockyResponse
import com.example.kotlinmockshowcase.data.main.server.ApiFactory
import com.example.kotlinmockshowcase.general.CoroutineContextProvider
import com.example.kotlinmockshowcase.general.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.net.UnknownHostException

/**
 * lifecycle.ViewModel that supports the MainFragment.
 */
class MainViewModel (private val postsRepository : PostsRepository, private val contextProvider: CoroutineContextProvider) : ViewModel() {

    // Fields used for filtering the Posts
    val filterUserId : MutableLiveData<Int?> = MutableLiveData(null)
    val postsOrder : MutableLiveData<Order?> = MutableLiveData(null)

    // Expose only LiveData
    private val result: MutableLiveData<NetworkResult> = MutableLiveData(NetworkResult.Loading)
    val networkResult : LiveData<NetworkResult> get () = result

    /**
     * LiveData of the MockyPost list. Couples kotlin coroutines with LiveData.
     *
     * The liveData(Dispatchers.IO) is a coroutine builder for IO intensive tasks.
     * In this build coroutine we call getPosts, which is suspendable and resumes when the
     * underlying retrofit network call finishes. We check the response and put it into liveData,
     * if applicable (when emit is called from this coroutine).
     *
     * The Dispatcher.IO maps to a pool of threads reserved for IO operations that will
     * not block CPU threads (Dispatchers.Default) or the main thread.
     */
    private val allMockyPosts : MutableLiveData<List<MockyPost>> = MutableLiveData()

    /**
     * Helper function for emitting the Result via the main thread
     */
    private suspend fun emitResult(networkResult: NetworkResult) {
        withContext(contextProvider.Main) {
            result.value = networkResult
        }
    }

    /**
     * Filtered LiveData with posts based on supplied filterUserId, or don't filter if null
     */
    private val filteredMockyPosts : LiveData<List<MockyPost>> = Transformations.switchMap(filterUserId) {
        id ->
            Transformations.map(allMockyPosts) {
                it.filter {
                    if (id != null) {
                        it.user_id == id
                    } else {
                        true
                    }
                }
            }
    }

    /**
     * Ordered LiveData with posts, based on postsOrder, or unordered if null
     */
    private val orderedMockyPosts : LiveData<List<MockyPost>> = Transformations.switchMap(postsOrder) {
            order -> orderPosts(order, filteredMockyPosts)
    }

    /**
     * For development: Easy to test only ordered or only filtered posts.
     */
    val mockyPosts get() = orderedMockyPosts

    fun refreshPosts() {
        viewModelScope.launch(contextProvider.IO) {
            try {
                val response: Response<MockyResponse> = postsRepository.getPosts()
                if (response.isSuccessful) {
                    Timber.d("getPosts success!")
                    if (response.body() == null) {
                        emitResult(NetworkResult.Error("Network call returned empty"))
                    } else {
                        emitResult(NetworkResult.Success)
                        withContext(contextProvider.Main) {
                            allMockyPosts.value = response.body()!!.mockyPosts
                        }
                    }
                } else {
                    emitResult(NetworkResult.Error(response.errorBody().toString()))
                    Timber.e(response.errorBody().toString())
                }
            } catch (e : UnknownHostException) {
                emitResult(NetworkResult.Error("Can't connect to host. Check internet connectivity"))
            } catch (e : Exception) {
                Timber.e(e)
                emitResult(NetworkResult.Error(e.localizedMessage))
            }
        }
    }


    /**
     * Helper function to order the posts
     */
    private fun orderPosts(order : Order?, input : LiveData<List<MockyPost>> ) : LiveData<List<MockyPost>> {
        return when (order) {
            Order.Ascending -> MutableLiveData<List<MockyPost>>(input.value?.sortedBy { it.published_at })
            Order.Descending -> MutableLiveData<List<MockyPost>>(input.value?.sortedByDescending { it.published_at })
            null -> input
        }
    }


    enum class Order {
        Ascending,
        Descending
    }
}

object MainViewModelFactory : ViewModelProvider.Factory {
    private val postsRepository = PostsRepository(ApiFactory.mockyApi)
    private val contextProvider = CoroutineContextProvider()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(postsRepository, contextProvider) as T
    }
}
