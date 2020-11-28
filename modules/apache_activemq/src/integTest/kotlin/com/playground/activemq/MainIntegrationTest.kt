package com.playground.activemq

import javax.jms.JMSException
import javax.jms.Session
import org.apache.activemq.ActiveMQConnectionFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class MainIntegrationTest {

    companion object {
        private val dockerImageName = DockerImageName.parse("rmohr/activemq:5.15.6-alpine")

        @Container
        private val container: GenericContainer<*> = GenericContainer<Nothing>(dockerImageName).apply {
            withExposedPorts(61616, 8161)
        }
    }

    private val connectionFactory = ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")

    @DisplayName("A consumer can listen for text messages from a producer using a callback")
    @Test
    fun testConsumersListenUsingCallbacks() = autoCloseTest { session ->
        val queue = session.createQueue("queue.chat.archive")
        val consumer = session.createConsumer(queue)
        val producer = session.createProducer(queue)
        var messagesReceived = 0

        with(consumer) {
            setMessageListener { messagesReceived += 1 }
            waitSomeTime()
        }
        with(producer) {
            send(session.createTextMessage("Hi John."))
            send(session.createTextMessage("Hi Mary! How is it going?"))
            send(session.createTextMessage("Fine. And you? :)"))
            waitSomeTime()
        }

        assertThat(messagesReceived).isEqualTo(3)

        consumer.close()
        producer.close()
    }

    @DisplayName("Message sent through a queue goes to one and only one consumer")
    @Test
    fun testThroughQueueToOnlyOneConsumer() = autoCloseTest { session ->
        val queue = session.createQueue("queue.chat.private")
        var messagesSentToDave = 0
        var messagesSentToJane = 0

        val daveConsumer = session.createConsumer(queue).also {
            it.setMessageListener { messagesSentToDave += 1 }
            waitSomeTime()
        }
        val janeConsumer = session.createConsumer(queue).also {
            it.setMessageListener { messagesSentToJane += 1 }
            waitSomeTime()
        }
        val producer = session.createProducer(queue).also {
            it.send(session.createTextMessage("Only dave might receive this"))
            waitSomeTime()
        }

        assertThat(messagesSentToDave).isEqualTo(1)
        assertThat(messagesSentToJane).isEqualTo(0)

        daveConsumer.close()
        janeConsumer.close()
        producer.close()
    }

    @DisplayName("Messages sent through a topic goes to all consumers")
    @Test
    fun testThroughTopicToAllConsumers() = autoCloseTest { session ->
        val topic = session.createTopic("topic.chat.public")
        var messagesSentToDevelopers = 0
        var messagesSentToManagers = 0

        val developersConsumer = session.createConsumer(topic).also {
            it.setMessageListener { messagesSentToDevelopers += 1 }
            waitSomeTime()
        }
        val managersConsumer = session.createConsumer(topic).also {
            it.setMessageListener { messagesSentToManagers += 1 }
            waitSomeTime()
        }
        val producer = session.createProducer(topic).also {
            it.send(session.createTextMessage("Hi developers and managers"))
            waitSomeTime()
        }

        assertThat(messagesSentToDevelopers).isEqualTo(1)
        assertThat(messagesSentToManagers).isEqualTo(1)

        developersConsumer.close()
        managersConsumer.close()
        producer.close()
    }

    private fun autoCloseTest(testBlock: (Session) -> Unit) {
        try {
            val connection = connectionFactory.createConnection()
            val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            connection.start()

            testBlock(session)

            session.close()
            connection.close()
        } catch (exception: JMSException) {
            fail(exception.message)
        }
    }

    private fun waitSomeTime(timeInMs: Long = 5000) = Thread.sleep(timeInMs)
}
