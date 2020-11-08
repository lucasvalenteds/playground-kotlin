package com.playground.corenlp

import edu.stanford.nlp.simple.Sentence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LemmatizationTest {

    @Test
    @DisplayName("Lemmas are all words and symbols lowercase in the phrase")
    fun testWhatLemmasAre() {
        val products = "bread, lettuce, tomatoes and mustard" // 7 elements
        val location = "you go to the supermarket" // 5 elements
        val sentence = Sentence("Please, bring $products when $location. Thanks!") // 7 elements

        assertThat(sentence.lemmas()).hasSize(19)
    }
}
