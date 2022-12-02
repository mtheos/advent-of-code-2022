package challenges.day01

import challenges.Challenge
import Utils

class CalorieCounting : Challenge {
  private val elfCalories: List<Int> by lazy {
    val input = Utils.readInput(this.javaClass, "input.txt")
    parseInput(input)
  }

  override fun preamble(): String = "Day 01 - Calorie Counting"
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
