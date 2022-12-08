package me.theos.aoc2022.challenges.day05

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.util.Stack

class SupplyStacks : Challenge {
  private val stacks: List<Stack<Char>>
  private val steps: List<Triple<Int, Int, Int>>

  init {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input).run {
      stacks = first
      steps = second
    }
  }

  override fun solveEasy(): String {
    val easyStacks = copyStacks()
    steps.forEach {(count, from, to) ->
      repeat(count) {
        easyStacks[to].push(easyStacks[from].pop())
      }
    }
    val items = easyStacks.map { it.pop() }.joinToString("")
    return "Part 1: $items"
  }

  override fun solveHard(): String {
    val hardStacks = copyStacks()
    steps.forEach {(count, from, to) ->
      hardStacks[from].takeLast(count).forEach { hardStacks[to].push(it) }
      repeat(count) {hardStacks[from].pop()}
    }
    val items = hardStacks.map { it.pop() }.joinToString("")
    return "Part 2: $items"
  }

  private fun copyStacks(): List<Stack<Char>> {
    return arrayListOf<Stack<Char>>().apply {
      stacks.forEach {
        val stack = Stack<Char>()
        it.forEach { item -> stack.push(item) }
        add(stack)
      }
    }
  }

  private fun parseInput(input: List<String>): Pair<List<Stack<Char>>, List<Triple<Int, Int, Int>>> {
    val stackInput = input.takeWhile { it.isNotEmpty() }
    val stepInput = input.subList(stackInput.size + 1, input.size)
    return Pair(parseStacks(stackInput), parseSteps(stepInput))
  }

  private fun parseStacks(input: List<String>): List<Stack<Char>> {
    val stackInput = input.asReversed()
    val numStacks = stackInput.first().replace(Regex(" +"), "").length
    val stacks = MutableList(numStacks) { Stack<Char>() }
    stackInput.subList(1, stackInput.size).forEach {
      var i = -1
      while (++i < stacks.size) {
        val item = getStackItem(it, i)
        if (item != ' ') {
          stacks[i].push(item)
        }
      }
    }
    return stacks
  }

  private fun getStackItem(row: String, n: Int): Char {
    val start = 4 * n // zero index
    return if (start + 1 < row.length) {
      row[start + 1]
    } else {
      ' '
    }
  }

  private fun parseSteps(stepInput: List<String>): List<Triple<Int, Int, Int>> {
    val pattern = Regex("\\w+ (\\d+) \\w+ (\\d+) \\w+ (\\d+)")
    return arrayListOf<Triple<Int, Int, Int>>().apply {
      stepInput.forEach {
        val result = pattern.matchEntire(it)!!
        val (count, from, to) = result.destructured.toList().map { i -> Integer.parseInt(i) }
        add(Triple(count, from - 1, to - 1))
      }
    }
  }
}
