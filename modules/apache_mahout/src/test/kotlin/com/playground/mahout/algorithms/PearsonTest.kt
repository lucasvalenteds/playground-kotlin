package com.playground.mahout.algorithms

import com.playground.mahout.util.Fixtures
import com.playground.mahout.util.Movie
import org.apache.mahout.cf.taste.impl.model.GenericDataModel
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PearsonTest {

    private val dataModel = GenericDataModel(Fixtures.CodeBased.userData)

    @DisplayName("It can find similarity between two users")
    @Test
    fun testSimilarityUsers() {
        val similarityEngine = PearsonCorrelationSimilarity(dataModel)

        val similarityRatio = similarityEngine.userSimilarity(Fixtures.CodeBased.joe.id, Fixtures.CodeBased.eva.id)

        assertThat(similarityRatio).isNaN()
    }

    @DisplayName("It can find similarity between two items")
    @Test
    fun testSimilarityItems() {
        val similarityEngine = PearsonCorrelationSimilarity(dataModel)

        val similarToBlackMirror = similarityEngine.itemSimilarity(Movie.BLACK_MIRROR.id, Movie.MR_ROBOT.id)

        assertThat(similarToBlackMirror).isNaN()
    }

    @DisplayName("It can find all items similar to a given one")
    @Test
    fun testSimilarItems() {
        val similarityEngine = PearsonCorrelationSimilarity(dataModel)

        val similarToBlackMirror = similarityEngine.allSimilarItemIDs(Movie.BLACK_MIRROR.id)

        assertThat(similarToBlackMirror).isEmpty()
    }
}
