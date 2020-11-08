package com.playground.coroutines

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoroutineDispatchersTest {

    @DisplayName("It defines in what threads a coroutine will run")
    @Test
    fun testDefinesTheThreadToUse() = runBlocking<Unit> {
        val dispatchers = mutableListOf<String>()
        val jobs = arrayListOf<Job>()
        jobs += GlobalScope.launch(Dispatchers.Unconfined) { dispatchers.add(Thread.currentThread().name) }
        jobs += GlobalScope.launch(Dispatchers.Default) { dispatchers.add(Thread.currentThread().name) }
        jobs += GlobalScope.launch(newSingleThreadContext("NewThread")) { dispatchers.add(Thread.currentThread().name) }

        jobs.forEach { it.join() }

        assertThat(dispatchers.filter { it.contains("Test worker") }).hasSize(1)
        assertThat(dispatchers.filter { it.contains("DefaultDispatcher") }).hasSize(1)
        assertThat(dispatchers.filter { it.contains("NewThread") }).hasSize(1)
    }

    @DisplayName("The dispatcher newSingleThreadContext runs on a new thread")
    @Test
    fun testNewSingleThread() = testDispatcher(newSingleThreadContext("NewThread"), "NewThread")

    @DisplayName("The dispatcher coroutineContext runs on the thread of the parent coroutine")
    @Test
    fun testCoroutineContext() = testDispatcher(newSingleThreadContext("CustomThread"), "CustomThread")

    private fun testDispatcher(context: CoroutineContext, name: String) = runBlocking<Unit> {
        val job = GlobalScope.async(context) {
            assertThat(Thread.currentThread().name).contains(name)
        }

        job.await()
    }
}
