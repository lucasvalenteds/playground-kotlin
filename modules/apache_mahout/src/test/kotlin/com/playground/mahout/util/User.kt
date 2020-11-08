package com.playground.mahout.util

data class User<T>(val id: Long, val ratings: List<Rating<T>>)
