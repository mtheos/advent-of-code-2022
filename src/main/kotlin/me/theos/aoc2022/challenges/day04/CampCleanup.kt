package me.theos.aoc2022.challenges.day04

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.util.regex.Pattern

class CampCleanup : Challenge {
  private val sections: List<Pair<Set<Int>, Set<Int>>> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun preamble(): String {
    val name = javaClass.simpleName.split(Pattern.compile("(?=\\p{Upper})")).joinToString(" ")
    val day= javaClass.name.substringAfter(".day").substringBefore(".")
    return "Day $day - $name"
  }

  override fun solveEasy(): String {
    var enclosed = 0
    sections.forEach {
      if (it.first.containsAll(it.second) || it.second.containsAll(it.first)) {
        enclosed++
      }
    }
    return "Part 1: $enclosed"
  }

  override fun solveHard(): String {
    var overlapping = 0
    sections.forEach {
      if ((it.first + it.second).size != it.first.size + it.second.size) {
        overlapping++
      }
    }
    return "Part 2: $overlapping"
  }
  private fun parseInput(input: List<String>): List<Pair<Set<Int>, Set<Int>>> {
    return arrayListOf<Pair<Set<Int>, Set<Int>>>().apply {
      input.forEach {line ->
        val (f1Start, f1End, f2Start, f2End)
        = line.split(',', limit = 2)
          .flatMap { it.split('-', limit = 2) }
          .map { Integer.parseInt(it) }
        add(Pair(IntRange(f1Start, f1End).toSet(), IntRange(f2Start, f2End).toSet()))
      }
    }
  }
}
