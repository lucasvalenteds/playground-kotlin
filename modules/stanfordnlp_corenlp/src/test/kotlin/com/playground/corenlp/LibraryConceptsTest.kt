package com.playground.corenlp

import edu.stanford.nlp.simple.Document
import edu.stanford.nlp.simple.Sentence
import edu.stanford.nlp.simple.SentimentClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LibraryConceptsTest {

    private val lovelaceBioFromWikipedia = """
            This set of events made Ada famous in Victorian society.
            Byron did not have a relationship with his daughter, and never saw her again.
            He died in 1824 when she was eight years old.
            Her mother was the only significant parental figure in her life.
            Ada was not shown the family portrait of her father (covered in green shroud) until her twentieth birthday.
            Her mother became Baroness Wentworth in her own right in 1856.
            """.trimIndent()

    @DisplayName("A Sentence is intended to represent a phrase")
    @Test
    fun testWhatSentenceIs() {
        val sentence = Sentence("Your welcome, thanks for having me.")

        assertThat(sentence.sentenceIndex()).isEqualTo(0)
    }

    @DisplayName("A Document is intended to be a group of sentences")
    @Test
    fun testWhatDocumentIs() {
        val document = Document(lovelaceBioFromWikipedia)

        assertThat(document.sentences()).hasSize(6)
    }

    @DisplayName("A document can be mapped as JSON")
    @Test
    fun testDocumentAsJSON() {
        val document = Document(lovelaceBioFromWikipedia)

        val sentencesAsJson = document.json()

        assertThat(sentencesAsJson).contains("index")
        assertThat(sentencesAsJson).contains("word")
        assertThat(sentencesAsJson).contains("originalText")
        assertThat(sentencesAsJson).contains("characterOffsetBegin")
        assertThat(sentencesAsJson).contains("characterOffsetEnd")
        assertThat(sentencesAsJson).contains("before")
        assertThat(sentencesAsJson).contains("after")
    }

    @DisplayName("Different levels sentiment can be interpreted based on a sentence")
    @Test
    fun testSentimentsCanBeRetrieved() {
        val positiveSentence = Sentence("Thank you so much, Mary. I'm very happy with your feedback.")
        val negativeSentence = Sentence("Although the code compiles, it does not do what is expected")

        assertThat(positiveSentence.sentiment()).isEqualTo(SentimentClass.POSITIVE)
        assertThat(negativeSentence.sentiment()).isEqualTo(SentimentClass.NEGATIVE)
    }

    @DisplayName("It can provide the grammatical category of words")
    @Test
    fun testGrammaticalCategories() {
        val properNoun = "NNP"
        val verb = "VBZ"
        val preposition = "IN"
        val possessiveNoun = "PRP$"
        val singularNoun = "NN"

        val sentence = Sentence("John lives in New York his boyfriend Mark")

        val tags = sentence.posTags()

        assertThat(tags).containsExactly(
                properNoun,
                verb,
                preposition,
                properNoun,
                properNoun,
                possessiveNoun,
                singularNoun,
                properNoun)
    }
}
