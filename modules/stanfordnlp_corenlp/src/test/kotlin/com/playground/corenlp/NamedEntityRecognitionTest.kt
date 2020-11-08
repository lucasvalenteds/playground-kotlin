package com.playground.corenlp

import edu.stanford.nlp.simple.Sentence
import edu.stanford.nlp.simple.Token
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Disabled
class NamedEntityRecognitionTest {

    @DisplayName("It can find names in a sentence")
    @Test
    fun testNaming() {
        val nerTokenForPersonName = "PERSON"
        val sentence = Sentence("Mary, John, Edgar and Angela were invited to Allan's party.")

        val personNames = sentence.tokens()
                .filter { it.nerTag() == nerTokenForPersonName }
                .map(Token::originalText)

        assertThat(personNames).containsAll(listOf("Mary", "John", "Edgar", "Angela", "Allan"))
    }

    @DisplayName("It can find cities and countries in a sentence")
    @Test
    fun testCities() {
        val (nerTokenForCities, nerTokenForCountries) = "CITY" to "COUNTRY"

        val sentence = Sentence("Yorke used to live in Seattle, but now she lives in Colombia.")
        val locations = sentence.tokens().map(Token::nerTag)

        val (seattle, colombia) = locations[5] to locations[12]

        assertThat(seattle).isEqualTo(nerTokenForCities)
        assertThat(colombia).isEqualTo(nerTokenForCountries)
    }
}
