package com.example.kotlinmockshowcase.general

/**
 * Helper class to propagate the result of the http calls.
 */
sealed class NetworkResult {
    object Loading : NetworkResult()
    object Success : NetworkResult()
    data class Error(val error: String): NetworkResult()
}