package com.playground.activemq

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import java.util.Properties
import java.util.Random
import java.util.Timer
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.Session
import javax.jms.TextMessage
import javax.naming.InitialContext
import kotlin.concurrent.scheduleAtFixedRate

enum class Currency {
    BRL, EUR, USD
}

data class Payment(
    val currency: Currency = Currency.USD,
    val total: Double = 0.00,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val from: Person,
    val to: Person
) {

    override fun toString(): String = "$timestamp : $from sent $total $currency to $to"
}

data class Person(val name: String = "") {

    override fun toString(): String = name
}

val persons = listOf("John", "Mary", "Dave", "Lara").map(::Person)

val transactions = persons
        .flatMap { name -> persons.map { Pair(name, it) } }
        .filter { it.first.name != it.second.name }

val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, false)!!

val decimalFormat = DecimalFormat(".##").also {
    val dotSymbol = DecimalFormatSymbols()
    dotSymbol.decimalSeparator = '.'

    it.decimalFormatSymbols = dotSymbol
}

val random = Random()

fun main(args: Array<String>) {
    Thread.sleep(5000) // Wait ActiveMQ container to be running

    // Step 1: Create a connection with ActiveMQ
    val context = InitialContext()
    val connectionFactory = context.lookup("ConnectionFactory") as ConnectionFactory
    val connection = connectionFactory.createConnection()
    connection.start()

    // Step 2: Create a consumer for a queue
    val properties = Properties()
    properties.load(Payment::class.java.classLoader.getResourceAsStream("jndi.properties"))

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val queue = context.lookup(properties.getProperty("queue.payments")) as Destination
    val consumer = session.createConsumer(queue)
    consumer.setMessageListener { rawMessage ->
        val textMessage = (rawMessage as TextMessage).text

        val payment = mapper.readValue<Payment>(textMessage)

        println("$payment\t(${rawMessage.jmsMessageID})")
    }

    // Step 3: Send a message programmatically or from the ActiveMQ dashboard
    val producer = session.createProducer(queue)

    val timer = Timer()
    timer.scheduleAtFixedRate(Date.from(Instant.now()), 500) {
        val person = transactions[random.nextInt(transactions.size - 1)]

        val payment = Payment(
                currency = Currency.values()[random.nextInt(3)],
                total = decimalFormat.format(random.doubles(0.01, 121.00).findFirst().asDouble).toDouble(),
                from = person.first,
                to = person.second)

        val paymentJson = mapper.writeValueAsString(payment)

        val message = session.createTextMessage(paymentJson)
        producer.send(message)
    }

    Thread.sleep(5000) // Wait before terminate the code

    // Step 4: Stop the code and close communication with ActiveMQ
    timer.cancel()

    session.close()
    connection.close()
    context.close()
}
