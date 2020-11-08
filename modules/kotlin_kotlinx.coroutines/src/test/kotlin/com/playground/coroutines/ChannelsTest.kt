package com.playground.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Disabled("Hanging test")
class ChannelsTest {

    @DisplayName("It uses method send and receive to transfer data")
    @Test
    fun testSend() = runBlocking<Unit> {
        val oneToTen = mutableListOf<Int>()
        val channel = Channel<Int>()
        GlobalScope.launch { for (number in (0..10)) channel.send(number) }

        repeat(10) { oneToTen.add(channel.receive()) }

        assertThat(oneToTen).hasSize(10)
    }

    @DisplayName("A channel can be closed")
    @Test
    fun testClosingChannel() = runBlocking<Unit> {
        val messages = mutableListOf<String>()
        val channel = Channel<String>()
        GlobalScope.launch {
            (1..10)
                    .takeWhile { it < 7 }
                    .map { channel.send("Message $it") }
                    .also { channel.close() }
        }

        for (message in channel) messages.add(message)

        assertThat(messages).hasSize(6)
    }

    @ExperimentalCoroutinesApi
    private fun CoroutineScope.produceFiveEvenNumbers() = produce {
        for (number in (0..5).filter { it % 2 == 0 }) {
            send(number)
        }
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @DisplayName("A producer can be used instead of a channel")
    @Test
    fun testProducerInsteadOfChannel() = runBlocking<Unit> {
        val numbers = mutableListOf<Int>()
        val evenNumberProducer = produceFiveEvenNumbers()

        evenNumberProducer.consumeEach { numbers.add(it) }

        assertThat(numbers).hasSize(3)
    }
}
