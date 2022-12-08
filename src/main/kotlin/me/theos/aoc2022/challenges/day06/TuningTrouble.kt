package me.theos.aoc2022.challenges.day06

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.util.LinkedList

class TuningTrouble : Challenge {
  private val signal: Iterable<Char> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val startMarker = findMarker(4, signal)
    return "Part 1: $startMarker"
  }

  override fun solveHard(): String {
    val startMarker = findMarker(14, signal)
    return "Part 2: $startMarker"
  }

  private fun findMarker(markerSize: Int, signal: Iterable<Char>): Int {
    val it = signal.iterator().withIndex()
    val seen = LinkedList<Char>()
    while (it.hasNext()) {
      val next = it.next()
      if (seen.size == markerSize) {
        seen.removeAt(0)
      }
      seen.add(next.value)
      if (seen.toSet().size == markerSize) {
        return next.index + 1
      }
    }
    return -1
  }

  private fun parseInput(input: List<String>): Iterable<Char> {
    return input[0].asIterable()
  }
}
