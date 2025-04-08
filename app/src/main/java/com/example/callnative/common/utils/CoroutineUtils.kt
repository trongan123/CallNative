package com.example.callnative.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

object CoroutineUtils {
    val numberOfCores = Runtime.getRuntime().availableProcessors()
    val semaphore = Semaphore(numberOfCores)
    private val supervisorJob = SupervisorJob()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    fun launchBackground(task: suspend () -> Unit) {
        backgroundScope.launch {
            semaphore.acquire()
            try {
                task()
            } finally {
                semaphore.release()
            }
        }
    }

    fun launchOnMain(task: suspend () -> Unit) = mainScope.launch { task() }

}
