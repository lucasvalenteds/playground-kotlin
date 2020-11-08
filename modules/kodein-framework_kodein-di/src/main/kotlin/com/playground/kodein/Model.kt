package com.playground.kodein

inline class Name(val value: String)
inline class Age(val value: Int)

data class Person(val name: Name, val age: Age)
