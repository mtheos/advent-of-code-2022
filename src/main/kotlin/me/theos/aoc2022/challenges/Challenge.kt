package me.theos.aoc2022.challenges

import java.util.regex.Pattern

interface Challenge : Comparable<Challenge> {
  fun preamble(): String {
    val name = javaClass.simpleName.split(Pattern.compile("(?=\\p{Upper})")).joinToString(" ")
    val day = javaClass.name.substringAfter(".day").substringBefore(".")
    return "Day $day - $name"
  }
  fun solveEasy(): String
  fun solveHard(): String
  override fun compareTo(other: Challenge): Int {
    val day = javaClass.name.substringAfter(".day").substringBefore(".")
    val otherDay = other.javaClass.name.substringAfter(".day").substringBefore(".")
    return Integer.parseInt(day) - Integer.parseInt(otherDay)
  }
}
