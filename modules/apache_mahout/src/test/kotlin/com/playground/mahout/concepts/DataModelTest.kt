package com.playground.mahout.concepts

import java.nio.file.Paths
import org.apache.mahout.cf.taste.impl.common.FastByIDMap
import org.apache.mahout.cf.taste.impl.model.GenericDataModel
import org.apache.mahout.cf.taste.impl.model.GenericPreference
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.model.PreferenceArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DataModelTest {

    @DisplayName("It represents the content and can be defined programmatically")
    @Test
    fun testDatasetIsContent() {
        val (joe, sam, eva) = Triple(1L, 2L, 3L)
        val (blackMirror, mrRobot) = 1L to 2L
        val (houseOfCards, theBlacklist) = 3L to 4L
        val userData = FastByIDMap<PreferenceArray>().apply {
            put(joe, GenericUserPreferenceArray(listOf(
                    GenericPreference(joe, blackMirror, 10F),
                    GenericPreference(joe, theBlacklist, 9F))))

            put(sam, GenericUserPreferenceArray(listOf(
                    GenericPreference(sam, blackMirror, 10F))))

            put(eva, GenericUserPreferenceArray(listOf(
                    GenericPreference(eva, blackMirror, 10F),
                    GenericPreference(eva, mrRobot, 8F),
                    GenericPreference(eva, houseOfCards, 5F))))
        }

        val dataModel = GenericDataModel(userData)

        assertThat(dataModel.numUsers).isEqualTo(3)
        assertThat(dataModel.numItems).isEqualTo(4)
        assertThat(dataModel.minPreference).isEqualTo(5F)
        assertThat(dataModel.maxPreference).isEqualTo(10F)
        assertThat(dataModel.userIDs.asSequence().toList()).containsExactly(joe, sam, eva)
    }

    @DisplayName("It can be read from a CSV file")
    @Test
    fun testReadFromCSV() {
        val dataSet = "concepts/sample.csv"

        val dataModel = FileDataModel(Paths.get("src/test/resources").resolve(dataSet).toAbsolutePath().toFile())

        assertThat(dataModel.userIDs.asSequence().toList()).containsAll(listOf(1L, 2L, 3L))
        assertThat(dataModel.itemIDs.asSequence().toList()).containsAll(listOf(10L, 11L, 12L, 17L, 18L))
        assertThat(dataModel.numUsers).isEqualTo(3)
    }
}
