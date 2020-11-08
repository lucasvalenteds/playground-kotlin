package com.playground.mahout.concepts

import com.playground.mahout.util.Fixtures
import org.apache.mahout.cf.taste.impl.model.GenericDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NeighborhoodTest {

    @DisplayName("It defines how users will be grouped")
    @Test
    fun testDefinesHowUsersAreGrouped() {
        val dataModel = GenericDataModel(Fixtures.FileBased.userData)
        val neighborhoodStrategy = ThresholdUserNeighborhood(0.2, PearsonCorrelationSimilarity(dataModel), dataModel)

        val evaNeighborhood = neighborhoodStrategy.getUserNeighborhood(Fixtures.FileBased.suzy.id)

        assertThat(evaNeighborhood).contains(Fixtures.FileBased.hanna.id) // Suzy and Hanna have similar ratings
    }

    @DisplayName("Users can be group by nearest similarity")
    @Test
    fun testNearestSimilarity() {
        val dataModel = GenericDataModel(Fixtures.FileBased.userData)
        val neighborhoodStrategy = NearestNUserNeighborhood(2, PearsonCorrelationSimilarity(dataModel), dataModel)

        val evaNeighborhood = neighborhoodStrategy.getUserNeighborhood(Fixtures.FileBased.suzy.id)

        assertThat(evaNeighborhood).hasSize(1) // Suzy and Edgar does not have similar ratings
        assertThat(evaNeighborhood).contains(Fixtures.FileBased.hanna.id) // Suzy and Hanna have similar ratings
    }
}
