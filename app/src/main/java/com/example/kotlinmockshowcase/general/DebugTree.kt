package com.example.kotlinmockshowcase.general

import timber.log.Timber

/**
 * Helper class for Timber that adds a lineNumber to the log.
 */
class DebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + "(" + element.lineNumber + ")"
    }
}