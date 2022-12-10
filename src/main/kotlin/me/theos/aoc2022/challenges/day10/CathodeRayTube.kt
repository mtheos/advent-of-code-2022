package me.theos.aoc2022.challenges.day10

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.util.LinkedList
import java.util.Queue

class CathodeRayTube : Challenge {
  private val instructions: List<Instruction> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val interestingCycles = LinkedList<Int>().apply { addAll(listOf(20, 60, 100, 140, 180, 220)) }
    val interestingInstructions = captureRegister(interestingCycles)
    var sum = 0
    interestingInstructions.forEach { entry ->
      sum += entry.key * (instructions
        .takeWhile { it !== entry.value }
        .filter { it.command == Command.AddX }
        .sumOf { it.value!! } + 1)
    }
    return "Part 1: $sum"
  }

  override fun solveHard(): String {
    val display = simulateDisplay()
    display.chunked(40).forEach { chunk ->
      chunk.forEach {
        print(it)
      }
      println()
    }
    return "Part 2: See above"
  }

  private fun captureRegister(interestingCycles: Queue<Int>): MutableMap<Int, Instruction> {
    val interestingInstructions = mutableMapOf<Int, Instruction>()
    var cycle = 0
    var nextInterestingCycle = interestingCycles.poll()
    instructions.forEach {
      if (nextInterestingCycle == null) {
        return@forEach
      }
      when (it.command) {
        Command.Noop -> {
          cycle++
          if (cycle == nextInterestingCycle || cycle + 1 == nextInterestingCycle) {
            // if the next cycle is noop it won't matter and if it's addx it won't complete
            interestingInstructions[nextInterestingCycle] = it
            nextInterestingCycle = interestingCycles.poll()
          }
        }
        Command.AddX -> {
          cycle += 2
          if (cycle == nextInterestingCycle || cycle - 1 == nextInterestingCycle) {
            interestingInstructions[nextInterestingCycle] = it
            nextInterestingCycle = interestingCycles.poll()
          }
        }
      }
    }
    return interestingInstructions
  }

  private fun simulateDisplay(): List<String> {
    val display = mutableListOf<String>().apply { repeat(240) { add(". ") } }
    var cycle = 0
    var register = 1
    instructions.forEach {
      when (it.command) {
        Command.Noop -> {
          maybeDraw(display, register, cycle)
          cycle++
        }
        Command.AddX -> {
          maybeDraw(display, register, cycle)
          maybeDraw(display, register, cycle + 1)
          cycle += 2
          register += it.value!!
        }
      }
    }
    return display
  }

  private fun maybeDraw(display: MutableList<String>, register: Int, cycle: Int) {
    if (IntRange(register - 1, register + 1).contains(cycle % 40)) {
      display[cycle] = "# "
    }
  }

  private fun parseInput(input: List<String>): List<Instruction> {
    return mutableListOf<Instruction>().apply {
      input.forEach { line ->
        when {
          line.startsWith("noop") -> add(Instruction(Command.Noop))
          line.startsWith("addx") -> add(Instruction(Command.AddX, Integer.parseInt(line.substring("addx ".length))))
        }
      }
    }
  }

  private data class Instruction(val command: Command, val value: Int? = null)
  private enum class Command {
    Noop,
    AddX
  }
}
