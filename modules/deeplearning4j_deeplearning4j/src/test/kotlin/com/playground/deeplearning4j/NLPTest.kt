package com.playground.deeplearning4j

import org.assertj.core.api.Assertions.assertThat
import org.datavec.api.util.ClassPathResource
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NLPTest {

    /**
     *  Output: Similarity of 10 words nearest to "day".
     *  ----------
     *  week    0.49504682421684265
     *  night   0.49285170435905457
     *  year    0.41608718037605286
     *  game    0.3712853193283081
     *  time    0.31413960456848145
     *  john    0.30833935737609863
     *  days    0.2664262652397156
     *  little  0.24771228432655334
     *  second  0.2474464625120163
     *  years   0.23966406285762787
     */
    @DisplayName("Word2Vec :: It can find N nearest words of a given word")
    @Test
    fun testWordToVector() {
        val lineIterator = LineSentenceIterator(ClassPathResource("nlp/sentences.txt").file)

        val tokenizerFactory = DefaultTokenizerFactory()
        tokenizerFactory.tokenPreProcessor = CommonPreprocessor()

        val vector = Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(lineIterator)
                .tokenizerFactory(tokenizerFactory)
                .build()

        vector.fit()

        // Save the trained model in a file to reuse or backup
        // WordVectorSerializer.writeWord2VecModel(vector, "output")

        val word = "day"
        val nearestWords = vector.wordsNearest(word, 5)
        val nearestSimilarities = nearestWords.map { vector.similarity(word, it) }

        assertThat(nearestWords).hasSize(5)
        assertThat(nearestWords).containsAll(listOf("week", "night", "year"))

        assertThat(nearestSimilarities).hasSize(5)
        assertThat(nearestSimilarities.first()).isGreaterThan(nearestSimilarities.last())
    }
}
