package me.theos.aoc2022.challenges.day03

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils

class RucksackReorganization : Challenge {
  private val ruckSacks: List<RuckSack> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    var priorities = 0
    ruckSacks.forEach {
      priorities += it.commonItems().sumOf { ch -> ch.priority() }
    }
    return "Part 1: $priorities"
  }

  override fun solveHard(): String {
    var priorities = 0
    ruckSacks.chunked(3).forEach {(one, two, three) ->
      priorities += one.allItems().intersect(two.allItems()).intersect(three.allItems()).sumOf { ch -> ch.priority() }
    }
    return "Part 2: $priorities"
  }

  private fun parseInput(input: List<String>): List<RuckSack> {
    return arrayListOf<RuckSack>().apply {
      input.forEach {
        val first = it.subSequence(0, it.length / 2)
        val second = it.subSequence(it.length / 2, it.length)
        add(RuckSack(first.asIterable().toList(), second.asIterable().toList()))
      }
    }
  }

  private class RuckSack(private val compartment1: List<Char>, private val compartment2: List<Char>) {
    fun commonItems(): Set<Char> {
      return compartment1.toSet().intersect(compartment2.toSet())
    }

    fun allItems(): Set<Char> {
      return compartment1.union(compartment2)
    }
  }
  private fun Char.priority(): Int {
    return if (this.isLowerCase()) {
      this.minus('a').inc()
    } else {
      this.minus('A').plus(27)
    }
  }
}
