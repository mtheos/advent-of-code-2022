package challenges.day02

import challenges.Challenge
import Utils
import challenges.day02.RpsResult.Draw
import challenges.day02.RpsResult.Lose
import challenges.day02.RpsResult.Win

class RockPaperScissors : Challenge {
  private val rpsRounds: List<RpsRound> by lazy {
    val input = Utils.readInput(this.javaClass, "input.txt")
    parseInput(input)
  }

  override fun preamble(): String = "Day 02 - Rock Paper Scissors"
  override fun solveEasy(): String {
    var score = 0
    rpsRounds.forEach {
      score += it.score()
    }
    return "Part 1: $score"
  }

  override fun solveHard(): String {
    var score = 0
    rpsRounds.forEach {
      score += it.forceScore()
    }
    return "Part 2: $score"
  }

  private fun parseInput(input: List<String>): List<RpsRound> {
    return arrayListOf<RpsRound>().apply {
      input.forEach {
        add(RpsRound(RpsChoice.from(it[0]), RpsChoice.from(it[2]), RpsChoice.forResult(it[0], it[2])))
      }
    }
  }
}

class RpsRound(private val opponent: RpsChoice, private val you: RpsChoice, private val forceResult: RpsChoice) {
  fun score(): Int = RpsResult.score(result(you, opponent)) + RpsChoice.score(you)
  fun forceScore(): Int = RpsResult.score(result(you, forceResult)) + RpsChoice.score(forceResult)

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

enum class RpsResult {
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


enum class RpsChoice {
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

    fun forResult(o: Char, y: Char): RpsChoice {
      val op = from(o)
      return when (RpsResult.from(y)) {
        Lose -> loseAgainst(op)
        Draw -> op
        Win -> winAgainst(op)
      }
    }
  }
}
