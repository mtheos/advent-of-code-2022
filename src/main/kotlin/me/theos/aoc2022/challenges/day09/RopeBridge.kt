package me.theos.aoc2022.challenges.day09

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.lang.AssertionError
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.min

class RopeBridge : Challenge {
  private val movements: List<Pair<Char, Int>> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val visited = solve(2)
    return "Part 1: ${visited.size}"
  }

  override fun solveHard(): String {
    val visited = solve(10)
    return "Part 2: ${visited.size}"
  }

  private lateinit var debugAction: DebugAction
  private fun solve(length: Int): Set<Pair<Int, Int>> {
    if (length < 2) {
      throw AssertionError()
    }
    val visited = mutableSetOf<Pair<Int, Int>>()
    val rope = mutableListOf<Pair<Int, Int>>().apply { repeat(length) { add(Pair(0, 0)) } }
    display(rope, visited, null, null)
    debugAction = DebugAction.Next
    movements.forEach { move ->
      if (debugAction != DebugAction.Continue) {
        debugAction = getAction(debugAction)
      }
      repeat(move.second) {
        rope[0] = moveHead(rope[0], move.first)
        display(rope, visited, move, it)
        if (debugAction == DebugAction.Step) {
          debugAction = getAction(debugAction)
        }
        for (i in 1 until rope.size) {
          val next = positionKnot(rope[i], rope[i - 1], move.first)
          if (next == rope[i]) {
            continue
          }
          rope[i] = next
          display(rope, visited, move, it)
          if (debugAction == DebugAction.Step) {
            debugAction = getAction(debugAction)
          }
        }
        visited.add(rope.last())
      }
    }
    display(rope, visited, null, null)
    debugAction = getAction(debugAction)
    return visited
  }

  private fun moveHead(head: Pair<Int, Int>, direction: Char): Pair<Int, Int> {
    return when (direction) {
      'U' -> Pair(head.first, head.second + 1)
      'D' -> Pair(head.first, head.second - 1)
      'R' -> Pair(head.first + 1, head.second)
      'L' -> Pair(head.first - 1, head.second)
      else -> throw AssertionError()
    }
  }

  private fun positionKnot(tail: Pair<Int, Int>, head: Pair<Int, Int>, direction: Char): Pair<Int, Int> {
    if (abs(tail.first - head.first) <= 1 && abs(tail.second - head.second) <= 1) {
      return tail
    }
    return when (direction) {
      'U' -> {
        val delta1 = getDelta(head.first, tail.first)
        val delta2 = getDelta(head.second, tail.second)
        Pair(tail.first + delta1, tail.second + delta2)
      }
      'D' -> {
        val delta1 = getDelta(head.first, tail.first)
        val delta2 = getDelta(head.second, tail.second)
        Pair(tail.first + delta1, tail.second + delta2)
      }
      'R' -> {
        val delta1 = getDelta(head.first, tail.first)
        val delta2 = getDelta(head.second, tail.second)
        Pair(tail.first + delta1, tail.second + delta2)
      }
      'L' -> {
        val delta1 = getDelta(head.first, tail.first)
        val delta2 = getDelta(head.second, tail.second)
        Pair(tail.first + delta1, tail.second + delta2)
      }
      else -> throw AssertionError()
    }
  }

  private fun getDelta(head: Int, tail: Int): Int {
    return if (head > tail) {
      1
    } else if (head < tail) {
      -1
    } else {
      0
    }
  }

  private fun parseInput(input: List<String>): List<Pair<Char, Int>> {
    return mutableListOf<Pair<Char, Int>>().apply {
      input.forEach { line ->
        add(line.split(" ").let { Pair(it[0][0], Integer.parseInt(it[1])) })
      }
    }
  }

  private var warned = false
  private val debug = false
  private fun display(rope: List<Pair<Int, Int>>, visited: Set<Pair<Int, Int>>, move: Pair<Char, Int>?, step: Int?) {
    if (!debug) {
      return
    }
    if (movements.size > 100) {
      if (!warned) {
        println("Not displaying the path for inputs > 100")
        warned = true
      }
      return
    }
    val x1 = min(min(rope.minOf { it.first }, visited.minOfOrNull { it.first } ?: Integer.MAX_VALUE) - 1, -5)
    val x2 = max(max(rope.maxOf { it.first }, visited.maxOfOrNull { it.first } ?: 0) + 1, 5)
    val y1 = min(min(rope.minOf { it.second }, visited.minOfOrNull { it.second } ?: Integer.MAX_VALUE) - 1, -5)
    val y2 = max(max(rope.maxOf { it.second }, visited.maxOfOrNull { it.second } ?: 0) + 1, 5)
    val display = mutableListOf<MutableList<String>>()
    repeat(y2 - y1 + 1) {
      display.add(mutableListOf<String>().apply { repeat(x2 - x1 + 1) { add(". ") } })
    }
    visited.forEach {
      display[it.second - y1][it.first - x1] = "# "
    }
    display[-y1][-x1] = "s "
    if (move != null && step != null) {
      display[-y1].addAll("  Move: ${move.first} ${move.second} (${step + 1})".split("").filter { it.isNotEmpty() })
    }
    for (i in rope.size - 1 downTo 1) {
      display[rope[i].second - y1][rope[i].first - x1] = "$i "
    }
    display[rope[0].second - y1][rope[0].first - x1] = "H "
    display.reversed().forEach { row ->
      row.forEach { col ->
        print(col)
      }
      println()
    }
  }

  private fun getAction(last: DebugAction): DebugAction {
    if (!debug) {
      return DebugAction.Continue
    }
    val line = readln()
    if (line.isEmpty()) {
      return last
    }
    return when (line.single()) {
      's' -> {
        DebugAction.Step
      }
      'c' -> {
        DebugAction.Continue
      }
      else -> {
        DebugAction.Next
      }
    }
  }
  private enum class DebugAction {
    Step,
    Continue,
    Next;
  }
}
