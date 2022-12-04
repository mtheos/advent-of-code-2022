package me.theos.aoc2022

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Challenges

fun main() {
  val challenges = Challenges.load()
  challenges.forEach {
    when (!it.isInterface && Challenge::class.java.isAssignableFrom(it)) {
      true -> {
        val challenge = it.getConstructor().newInstance()
        (challenge as Challenge).run {
          println(preamble())
          println("   ${solveEasy()}")
          println("   ${solveHard()}")
          println()
        }
      }
      false -> {
        // nop
      }
    }
  }
}
