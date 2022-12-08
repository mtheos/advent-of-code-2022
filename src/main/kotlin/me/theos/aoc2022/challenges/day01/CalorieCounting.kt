package me.theos.aoc2022.challenges.day01

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils

class CalorieCounting : Challenge {
  private val elfCalories: List<Int> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    return "Part 1: ${elfCalories.max()}"
  }

  override fun solveHard(): String {
    return "Part 2: ${elfCalories.sortedDescending().take(3).sum()}"
  }

  private fun parseInput(input: List<String>): List<Int> {
    val elfCalories = arrayListOf<Int>()
    var elf = 0;
    input.forEach {
      when (it.length) {
        0 -> {
          elfCalories.add(elf)
          elf = 0
        }
        else -> {
          elf += Integer.parseInt(it)
        }
      }
    }
    return elfCalories
  }
}
