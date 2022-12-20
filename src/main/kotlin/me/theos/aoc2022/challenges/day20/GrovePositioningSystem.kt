package me.theos.aoc2022.challenges.day20

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import kotlin.math.abs

class GrovePositioningSystem : Challenge {
  private val decryptionKey = 811_589_153
  private val encrypted: List<Number> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val workingSet = encrypted.map { it.copy() }.toMutableList()
    jumble(encrypted, workingSet)
    val zeroIdx = workingSet.indexOf(workingSet.first { it.value == 0L })
    val idx1000 = (zeroIdx + 1000) % workingSet.size
    val idx2000 = (zeroIdx + 2000) % workingSet.size
    val idx3000 = (zeroIdx + 3000) % workingSet.size
    val sum = workingSet[idx1000].value + workingSet[idx2000].value + workingSet[idx3000].value
    return "Part 1: $sum"
  }

  override fun solveHard(): String {
    val keySet = encrypted.map { it.copy(value = it.value * decryptionKey) }
    val workingSet = keySet.toMutableList()
    repeat(10) {
      jumble(keySet, workingSet)
    }
    val zeroIdx = workingSet.indexOf(workingSet.first { it.value == 0L })
    val idx1000 = (zeroIdx + 1000) % workingSet.size
    val idx2000 = (zeroIdx + 2000) % workingSet.size
    val idx3000 = (zeroIdx + 3000) % workingSet.size
    val sum = workingSet[idx1000].value + workingSet[idx2000].value + workingSet[idx3000].value
    return "Part 2: $sum"
  }

  private fun jumble(keySet: List<Number>, workingSet: MutableList<Number>) {
    keySet.forEach {
      val idx = workingSet.indexOf(it)
      if (idx == -1) {
        throw AssertionError()
      }
      workingSet.remove(it)
      val newI = when  {
        idx + it.value > 0L -> (idx + it.value) % workingSet.size
        idx + it.value < 0L -> ((idx + it.value + ((abs(it.value) / workingSet.size) + 1) * workingSet.size) % workingSet.size)
        else -> workingSet.size
      }.toInt()
      workingSet.add(newI, it)
    }
  }

  private fun parseInput(input: List<String>): List<Number> {
    return input.withIndex().map { Number(it.value.toLong(), it.index) }
  }

  private data class Number(val value: Long, val idx: Int) {
    override fun toString(): String {
      return "$value"
    }
  }
}
