package me.theos.aoc2022.challenges

interface Challenge {
  fun preamble(): String
  fun solveEasy(): String
  fun solveHard(): String
}
