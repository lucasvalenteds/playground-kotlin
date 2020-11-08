package com.playground.coroutines

import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class CoroutineBuildersTest {

    @Nested
    inner class MainBlocks {

        @DisplayName("The runBlocking block fires and blocks the main thread")
        @Test
        fun testRunBlockingRunsOnMainThread() {
            val calls = arrayListOf<Int>()

            runBlocking {
                delay(1000L)
                calls.add(1)
            }
            calls.add(2)

            assertThat(calls).hasSize(2)
            assertThat(calls).containsAll(listOf(1, 2))
        }

        @DisplayName("The launch block is fire and forget")
        @Test
        fun testLaunchIsFireAndForget() {
            val calls = arrayListOf<Int>()

            GlobalScope.launch {
                delay(3000L)
                calls.add(1)
            }
            calls.add(2)

            assertThat(calls).hasSize(1)
            assertThat(calls).contains(2)
        }

        @DisplayName("The async block must be awaited and is meant to be used for concurrency")
        @Test
        fun testAsyncMustBeAwaited() = runBlocking<Unit> {
            val calls = arrayListOf<Int>()
            val job = GlobalScope.async {
                delay(2000L)
                calls.add(1)
            }

            job.await()
            calls.add(2)

            assertThat(calls).hasSize(2)
            assertThat(calls).containsAll(listOf(1, 2))
        }

        @DisplayName("The thread block is meant to be used instead of manual thread handling")
        @Test
        fun testThreadBlock() {
            val calls = arrayListOf<Int>()
            val job = thread {
                Thread.sleep(2000L)
                calls.add(1)
            }

            job.join()
            calls.add(2)

            assertThat(calls).hasSize(2)
            assertThat(calls).containsAll(listOf(1, 2))
        }

        @DisplayName("The delay function is meant to a lightweight Thread dot sleep")
        @RepeatedTest(5)
        fun testDelayFunction() {
            runBlocking {
                try {
                    (0..100_000)
                            .map { GlobalScope.launch { delay(1000L) } }
                            .forEach { it.join() }

                    assertThat(true).isTrue()
                } catch (exception: OutOfMemoryError) {
                    fail("Joining 100000 threads using coroutines should be possible")
                }
            }
        }
    }

    @Nested
    inner class SubBlocks {

        @DisplayName("The repeat block is a concurrent for and provides an index")
        @Test
        fun testRepeatBlock() = runBlocking<Unit> {
            val timesRepeated = mutableListOf<Int>()

            repeat(10) { timesRepeated += it }

            assertThat(timesRepeated).hasSize(10)
        }

        @DisplayName("The withContext block can be used to run a non cancellable operation")
        @Test
        fun testWithContextBlock() = runBlocking<Unit> {
            var wasCanceled = false

            val job = GlobalScope.launch {
                try {
                    repeat(50) { delay(250L) }
                } finally {
                    withContext(NonCancellable) {
                        delay(1000L)
                        wasCanceled = true
                    }
                }
            }

            assertThat(wasCanceled).isFalse()
            delay(1000L)
            assertThat(wasCanceled).isFalse()
            job.cancelAndJoin()
            assertThat(wasCanceled).isTrue()
        }

        @DisplayName("The measureTime Millis and Nano blocks returns a long with total time executed")
        @Test
        fun testMeasureTimeBlock() = runBlocking<Unit> {
            val milliseconds = measureTimeMillis { delay(500L) }
            val nanoseconds = measureNanoTime { delay(250L) }

            val totalTime = milliseconds + TimeUnit.NANOSECONDS.toMillis(nanoseconds)

            assertThat(totalTime).isGreaterThanOrEqualTo(750L)
        }

        @DisplayName("The buildSequence provides yield function")
        @Test
        fun testBuildSequence() = runBlocking<Unit> {
            val evenNumbersGenerator = sequence {
                var firstEvenNumber = 0
                while (true) {
                    yield(firstEvenNumber)
                    firstEvenNumber += 2
                }
            }
            val iterator = evenNumbersGenerator.iterator()

            val numbers = mutableListOf<Int>()
            repeat(8) { numbers.add(iterator.next()) }

            assertThat(numbers).hasSize(8)
            assertThat(numbers).containsAll(listOf(0, 2, 4, 6, 8, 10, 12, 14))
        }
    }

    @Nested
    inner class Usage {

        @DisplayName("The coroutine block returns a Job instance")
        @Test
        fun testCoroutineBlockReturnsJob() = runBlocking<Unit> {
            val job = GlobalScope.async {
                delay(1000L)
                "Hello World after wait 1 second"
            }

            assertThat(job.isActive).isTrue()
            assertThat(job.isCompleted).isFalse()

            job.await()

            assertThat(job.isActive).isFalse()
            assertThat(job.isCompleted).isTrue()
        }

        private suspend fun waitOneSecondInBackground() = delay(1000L)

        @DisplayName("The functions must be marked with suspend keyword to be suspended")
        @Test
        fun testFunctionMarkedAsSuspended() = runBlocking<Unit> {
            val time = measureTimeMillis { waitOneSecondInBackground() }

            assertThat(time).isBetween(1000L, 1050L)
        }
    }
}
