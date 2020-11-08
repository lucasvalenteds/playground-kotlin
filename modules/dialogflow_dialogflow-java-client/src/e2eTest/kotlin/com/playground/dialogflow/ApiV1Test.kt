package com.playground.dialogflow

import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.model.AIRequest
import org.assertj.core.api.Assertions.assertThat
import java.io.FileInputStream
import java.util.Properties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ApiV1Test {

    private val accessToken = with(Properties()) {
        load(FileInputStream("local.properties"))
        getProperty("DIALOGFLOW_TOKEN")
    }
    private val config = AIConfiguration(accessToken)
    private val service = AIDataService(config)

    @DisplayName("It can process text intents")
    @Test
    fun testTextIntents() {
        val priceInDollars = 50

        val result = service.request(AIRequest("$priceInDollars dollars in pounds")).result

        val priceToConvert = result.parameters["unit-currency"]!!.asJsonArray[0].asJsonObject["amount"].asDouble

        val conversionResult = when (result.metadata.intentName) {
            "dollar-to-pound" -> priceToConvert.times(4)
            else -> priceToConvert
        }

        assertThat(conversionResult).isEqualTo(200.0)
    }
}
