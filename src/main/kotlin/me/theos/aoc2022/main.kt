package me.theos.aoc2022

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Challenges

fun main() {
  val challenges = Challenges.load()
  challenges.filter { !it.isInterface && Challenge::class.java.isAssignableFrom(it) }
    .map { it.getConstructor().newInstance() as Challenge }
    .sortedBy { it }.last().let { challenge ->
      challenge.run {
        println(preamble())
        println("   ${solveEasy()}")
        println("   ${solveHard()}")
        println()
      }
    }
}
