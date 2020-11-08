package com.playground.mahout.examples

import java.nio.file.Paths
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MovieRatingTest {

    @DisplayName("The rating of a movie can be predicted for a user")
    @Test
    fun testItWorks() {
        val dataSet = Paths.get("src/test/resources/examples/users-and-movies-rating.csv").toAbsolutePath()
        val dataModel = FileDataModel(dataSet.toFile())
        val userSimilarity = PearsonCorrelationSimilarity(dataModel)
        val threshold = 0.2
        val userNeighborhood = ThresholdUserNeighborhood(threshold, userSimilarity, dataModel)

        val predictor = GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity)
        val (userId, howMany) = 2L to 3
        val recommendations = predictor.recommend(userId, howMany).map { it.itemID to it.value }

        assertThat(recommendations).hasSize(howMany)
        assertThat(recommendations).contains(12L to 4.8328104F)
        assertThat(recommendations).contains(13L to 4.6656213F)
        assertThat(recommendations).contains(14L to 4.331242F)
    }
}
