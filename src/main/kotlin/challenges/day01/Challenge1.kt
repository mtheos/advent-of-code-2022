package challenges.day01

import Utils

class Challenge1 {
  fun solve() {
    val input = Utils.readInput(this.javaClass, "input.txt");
    val elfCalories = parseInput(input)
    part1(elfCalories)
    part2(elfCalories)
  }

  private fun part1(elfCalories: List<Int>) {
    println("Part 1: ${elfCalories.max()}")
  }

  private fun part2(elfCalories: List<Int>) {
    println("Part 2: ${elfCalories.sortedDescending().take(3).sum()}")
  }

  fun parseInput(input: List<String>): List<Int> {
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
