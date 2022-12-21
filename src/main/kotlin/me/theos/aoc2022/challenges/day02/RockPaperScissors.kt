package me.theos.aoc2022.challenges.day02

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import me.theos.aoc2022.challenges.day02.RockPaperScissors.RpsResult.Draw
import me.theos.aoc2022.challenges.day02.RockPaperScissors.RpsResult.Lose
import me.theos.aoc2022.challenges.day02.RockPaperScissors.RpsResult.Win

class RockPaperScissors : Challenge {
  private val rpsRoundsEasy: List<RpsRound> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInputEasy(input)
  }
  private val rpsRoundsHard: List<RpsRound> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInputHard(input)
  }

  override fun solveEasy(): String {
    var score = 0
    rpsRoundsEasy.forEach {
      score += it.score()
    }
    return "Part 1: $score"
  }

  override fun solveHard(): String {
    var score = 0
    rpsRoundsHard.forEach {
      score += it.score()
    }
    return "Part 2: $score"
  }

  private fun parseInputEasy(input: List<String>): List<RpsRound> {
    return arrayListOf<RpsRound>().apply {
      input.forEach {
        add(RpsRound(RpsChoice.from(it[0]), RpsChoice.from(it[2])))
      }
    }
  }

  private fun parseInputHard(input: List<String>): List<RpsRound> {
    return arrayListOf<RpsRound>().apply {
      input.forEach {
        add(RpsRound(RpsChoice.from(it[0]), RpsChoice.forResult(it[0], it[2])))
      }
    }
  }

  private class RpsRound(private val opponent: RpsChoice, private val you: RpsChoice) {
    fun score(): Int = RpsResult.score(result(you, opponent)) + RpsChoice.score(you)

    private fun result(y: RpsChoice, o: RpsChoice): RpsResult {
      if (y == o) {
        return Draw
      }
      return if (RpsChoice.winAgainst(o) == y) {
        Win
      } else {
        Lose
      }
    }
  }

  private enum class RpsResult {
    Lose,
    Draw,
    Win;

    companion object {
      fun score(s: RpsResult): Int {
        return when (s) {
          Lose -> 0
          Draw -> 3
          Win -> 6
        }
      }
      fun from(s: Char): RpsResult {
        return when (s) {
          'X' -> Lose
          'Y' -> Draw
          'Z' -> Win
          else -> throw AssertionError()
        }
      }
    }
  }


  private enum class RpsChoice {
    Rock,
    Paper,
    Scissors;

    companion object {
      fun from(s: Char): RpsChoice {
        return when (s) {
          'A', 'X' -> Rock
          'B', 'Y' -> Paper
          'C', 'Z' -> Scissors
          else -> throw AssertionError()
        }
      }

      fun score(s: RpsChoice): Int {
        return when (s) {
          Rock -> 1
          Paper -> 2
          Scissors -> 3
        }
      }

      fun winAgainst(s: RpsChoice): RpsChoice {
        return when (s) {
          Rock -> Paper
          Paper -> Scissors
          Scissors -> Rock
        }
      }

      fun loseAgainst(s: RpsChoice): RpsChoice {
        return when (s) {
          Rock -> Scissors
          Paper -> Rock
          Scissors -> Paper
        }
      }

      fun forResult(o: Char, r: Char): RpsChoice {
        val op = from(o)
        return when (RpsResult.from(r)) {
          Lose -> loseAgainst(op)
          Draw -> op
          Win -> winAgainst(op)
        }
      }
    }
  }
}
