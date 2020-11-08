package com.playground.mahout.util

import org.apache.mahout.cf.taste.impl.common.FastByIDMap
import org.apache.mahout.cf.taste.impl.model.GenericPreference
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray
import org.apache.mahout.cf.taste.model.PreferenceArray

object Fixtures {

    object CodeBased {
        val joe = User(1L, listOf(
                Rating(Movie.BLACK_MIRROR, 10F),
                Rating(Movie.THE_BLACKLIST, 9F)))

        val sam = User(2L, listOf(
                Rating(Movie.BLACK_MIRROR, 10F)))

        val eva = User(3L, listOf(
                Rating(Movie.BLACK_MIRROR, 10F),
                Rating(Movie.MR_ROBOT, 8F),
                Rating(Movie.HOUSE_OF_CARDS, 5F)))

        val userData = listOf(joe, sam, eva)
                .map {
                    it.id to it.ratings.map { rating: Rating<Movie> ->
                        GenericPreference(it.id, rating.item.id, rating.rate)
                    }
                }
                .map { it.first to GenericUserPreferenceArray(it.second) }
                .fold(FastByIDMap<PreferenceArray>(), { acc, pair -> acc.put(pair.first, pair.second).let { acc } })
    }

    object FileBased {
        val suzy = User(1L, listOf(
                Rating(Book.PRIDE_AND_PREJUDICE, 1.0F),
                Rating(Book.CLEAN_CODE, 2.0F),
                Rating(Book.ANIMAL_FARM, 5.0F)))

        val edgar = User(2L, listOf(
                Rating(Book.THE_HOBBIT, 1.0F),
                Rating(Book.SANDMAN, 5.0F)))

        val hanna = User(3L, listOf(
                Rating(Book.CLEAN_CODE, 2.5F),
                Rating(Book.ANIMAL_FARM, 4.5F)))

        val userData = listOf(suzy, edgar, hanna)
                .map {
                    it.id to it.ratings.map { rating: Rating<Book> ->
                        GenericPreference(it.id, rating.item.id, rating.rate)
                    }
                }
                .map { it.first to GenericUserPreferenceArray(it.second) }
                .fold(FastByIDMap<PreferenceArray>(), { acc, pair -> acc.put(pair.first, pair.second).let { acc } })
    }
}
