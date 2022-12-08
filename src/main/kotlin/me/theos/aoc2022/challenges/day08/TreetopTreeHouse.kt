package me.theos.aoc2022.challenges.day08

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.util.regex.Pattern

class TreetopTreeHouse : Challenge {
  private val forrest: List<List<Int>> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun preamble(): String {
    val name = javaClass.simpleName.split(Pattern.compile("(?=\\p{Upper})")).joinToString(" ")
    val day = javaClass.name.substringAfter(".day").substringBefore(".")
    return "Day $day - $name"
  }

  override fun solveEasy(): String {
    var visibleTrees = 0
    for (i in forrest.indices) {
      for (j in forrest[i].indices) {
        if (isVisible(i, j)) {
          visibleTrees++
        }
      }
    }
    return "Part 1: $visibleTrees"
  }

  override fun solveHard(): String {
    var mostScenic = 0
    for (i in forrest.indices) {
      for (j in forrest[i].indices) {
        mostScenic = maxOf(mostScenic, scenicScore(i, j))
      }
    }
    return "Part 2: $mostScenic"
  }

  private fun isVisible(i: Int, j: Int): Boolean {
    return lookTop(i, j).first || lookBottom(i, j).first || lookLeft(i, j).first || lookRight(i, j).first
  }

  private fun scenicScore(i: Int, j: Int): Int {
    return lookTop(i, j).second * lookBottom(i, j).second * lookLeft(i, j).second * lookRight(i, j).second
  }

  private fun lookTop(i: Int, j: Int): Pair<Boolean, Int> {
    val height = forrest[i][j]
    for (n in (i - 1) downTo 0) {
      if (forrest[n][j] >= height) {
        return Pair(false, i - n)
      }
    }
    return Pair(true, i)
  }

  private fun lookBottom(i: Int, j: Int): Pair<Boolean, Int> {
    val height = forrest[i][j]
    for (n in (i + 1) until forrest.size) {
      if (forrest[n][j] >= height) {
        return Pair(false, n - i)
      }
    }
    return Pair(true, forrest.size - i - 1)
  }

  private fun lookLeft(i: Int, j: Int): Pair<Boolean, Int> {
    val height = forrest[i][j]
    for (n in (j - 1) downTo 0) {
      if (forrest[i][n] >= height) {
        return Pair(false, j - n)
      }
    }
    return Pair(true, j)
  }

  private fun lookRight(i: Int, j: Int): Pair<Boolean, Int> {
    val height = forrest[i][j]
    for (n in (j + 1) until forrest[i].size) {
      if (forrest[i][n] >= height) {
        return Pair(false, n - j)
      }
    }
    return Pair(true, forrest[i].size - j - 1)
  }

  private fun parseInput(input: List<String>): List<List<Int>> {
    return mutableListOf<List<Int>>().apply {
      input.forEach { row ->
        add(row.asIterable().map(Char::digitToInt))
      }
    }
  }
}
