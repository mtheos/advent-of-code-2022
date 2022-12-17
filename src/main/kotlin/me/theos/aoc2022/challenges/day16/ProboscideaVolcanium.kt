package me.theos.aoc2022.challenges.day16

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.lang.AssertionError
import java.util.regex.Pattern

class ProboscideaVolcanium : Challenge {
  private val start by lazy { valves.first { it.name == "AA" } }
  private val valves: List<Valve>
  private val usefulValves: List<Valve>

  init {
    val input = Utils.getChallengeInput(this.javaClass)
    valves = parseInput(input)
    usefulValves = valves.filter { it.flow > 0 || it === start }
  }

  override fun solveEasy(): String {
    val startState = listOf(listOf(start))
    val maxReleased = ohGodTheyAreMultiplying(startState, 30, valves.count { it.flow > 0 })
    return "Part 1: $maxReleased"
  }

  override fun solveHard(): String {
    val startState = listOf(listOf(start), listOf(start))
    val maxReleased = ohGodTheyAreMultiplying(startState, 26, valves.count { it.flow > 0 })
    return "Part 2: $maxReleased"
  }

  private fun ohGodTheyAreMultiplying(startState: List<List<Valve>>, totalTime: Int, maxPossibleOpen: Int): Int {
    var bestPaths = listOf(Path(startState, mapOf(), totalTime))
    var time = 1

    while (time < totalTime) {
      val nextPaths = mutableListOf<Path>()
      for (currentPath in bestPaths) {
        if (currentPath.opened.size == maxPossibleOpen) {
          continue
        }
        val lastN = currentPath.lastN()
        val currentValvesN = currentPath.allValves

        if (lastN.any { it.flow > 0 && !currentPath.opened.containsKey(it) }) {
          val opened = currentPath.opened.toMutableMap()

          val possible = lastN.zip(currentValvesN).map {
            val last = it.first
            val currentValves = it.second

            if (last.flow > 0 && !currentPath.opened.containsKey(last)) {
              opened[last] = time
              listOf(currentValves + last)
            } else {
              last.neighbours.map { v ->
                currentValves + v
              }
            }
          }

          cartesianProduct(possible).forEach {
            nextPaths.add(Path(it, opened, totalTime))
          }
        }

        cartesianProduct(lastN.map { it.neighbours })
          .filter { it.toSet().size == it.size }
          .forEach {
            val newState = mutableListOf<List<Valve>>()
            it.indices.forEach { i ->
              newState.add(currentValvesN[i] + it[i])
            }
            nextPaths.add(Path(newState, currentPath.opened, totalTime))
          }
      }

      bestPaths = nextPaths.sortedByDescending { it.total }.take(10_000)
      time++
    }
    return bestPaths.first().total
  }

  // Way to big brain for me
  // https://stackoverflow.com/a/62270662/5699459
  private fun <T> cartesianProduct(iterables: List<List<T>>): Sequence<List<T>> = sequence {
    require(iterables.map { it.size.toLong() }.reduce(Long::times) <= Int.MAX_VALUE) {
      "Cartesian product function can produce result whose size does not exceed Int.MAX_VALUE"
    }

    val numberOfIterables = iterables.size
    val lstLengths = ArrayList<Int>()
    val lstRemaining = ArrayList(listOf(1))

    iterables.reversed().forEach {
      lstLengths.add(0, it.size)
      lstRemaining.add(0, it.size * lstRemaining[0])
    }

    val nProducts = lstRemaining.removeAt(0)

    (0 until nProducts).forEach { product ->
      val result = ArrayList<T>()
      (0 until numberOfIterables).forEach { iterableIndex ->
        val elementIndex = product / lstRemaining[iterableIndex] % lstLengths[iterableIndex]
        result.add(iterables[iterableIndex][elementIndex])
      }
      yield(result.toList())
    }
  }

  private fun parseInput(input: List<String>): List<Valve> {
    val pattern = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)")
    val parsed = mutableListOf<Triple<String, Int, List<String>>>().apply {
      input.forEach {
        val m = pattern.matcher(it)
        when (m.matches()) {
          true -> {
            val name = m.group(1)
            val flow = m.group(2).toInt()
            val neighbours = m.group(3).split(", ")
            add(Triple(name, flow, neighbours))
          }
          false -> throw AssertionError()
        }
      }
    }
    val valves = mutableListOf<Valve>().apply {
      parsed.forEach {
        add(Valve(it.first, it.second, mutableListOf()))
      }
    }
    valves.zip(parsed).forEach {
      val valve = it.first
      val neighbours = it.second.third
      (valve.neighbours as MutableList).apply {
        neighbours.forEach { n ->
          add(valves.first { v -> v.name == n })
        }
      }
    }
    return valves
  }

  private data class Path(val allValves: List<List<Valve>>, val opened: Map<Valve, Int>, val totalTime: Int) {
    fun lastN(): List<Valve> {
      return allValves.map { it.last() }
    }

    val total: Int
      get() = opened.map { (valve, time) -> (totalTime - time) * valve.flow }.sum()
  }

  private data class Valve(val name: String, val flow: Int, val neighbours: List<Valve>) {
    override fun hashCode(): Int {
      return name.hashCode()
    }

    override fun toString(): String {
      return "Valve{name=$name, flow=$flow}"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Valve

      if (name != other.name) return false

      return true
    }
  }
}
