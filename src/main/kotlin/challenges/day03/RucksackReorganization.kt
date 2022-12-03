package challenges.day03

import challenges.Challenge
import Utils
import java.util.regex.Pattern

class RucksackReorganization : Challenge {
  private val ruckSacks: List<RuckSack> by lazy {
    val input = Utils.readInput(this.javaClass, "input.txt")
    parseInput(input)
  }

  override fun preamble(): String {
    val name = javaClass.simpleName.split(Pattern.compile("(?=\\p{Upper})")).joinToString(" ")
    val day= javaClass.name.substringAfter(".day").substringBefore(".")
    return "Day $day - $name"
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
    ruckSacks.chunked(3).forEach {(one,two,three) ->
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
}

class RuckSack(private val compartment1: List<Char>, private val compartment2: List<Char>) {
  fun commonItems(): Set<Char> {
    return compartment1.toSet().intersect(compartment2.toSet())
  }

  fun allItems(): Set<Char> {
    return compartment1.union(compartment2)
  }
}

fun Char.priority(): Int {
  return if (this.isLowerCase()) {
    this.minus('a').inc()
  } else {
    this.minus('A').plus(27)
  }
}
