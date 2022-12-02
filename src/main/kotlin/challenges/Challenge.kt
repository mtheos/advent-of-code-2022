package challenges

interface Challenge {
  fun preamble(): String
  fun solveEasy(): String
  fun solveHard(): String
}
