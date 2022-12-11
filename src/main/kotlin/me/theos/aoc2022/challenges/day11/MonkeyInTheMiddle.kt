package me.theos.aoc2022.challenges.day11

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.lang.AssertionError
import java.util.LinkedList
import kotlin.properties.Delegates

class MonkeyInTheMiddle : Challenge {
  private val monkeys: List<Monkey> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    monkeys.forEach(Monkey::reset)
    monkeys.forEach {
      it.adjustWorry = { item: Item -> Math.floorDiv(item.worry, 3L) }
    }
    // extra factor of 3 to account for the worry function
    Monkey.maxWorry = 3 * monkeys.map { it.throwMod }.reduce { acc, it -> acc * it }
    val inspectionFactor = monkeyBusiness(20)
    return "Part 1: $inspectionFactor"
  }

  override fun solveHard(): String {
    monkeys.forEach(Monkey::reset)
    monkeys.forEach {
      it.adjustWorry = { item -> item.worry }
    }
    Monkey.maxWorry = monkeys.map { it.throwMod }.reduce { acc, it -> acc * it }
    val inspectionFactor = monkeyBusiness(10_000)
    return "Part 2: $inspectionFactor"
  }

  private fun monkeyBusiness(rounds: Int): Long {
    repeat(rounds) {
      monkeys.forEach {
        for (n in monkeys.indices) {
          while (it.hasItems) {
            val throwTo = it.inspect()
            monkeys[throwTo.first].items.add(throwTo.second)
          }
        }
      }
    }
    return monkeys.sortedByDescending { it.inspected }.take(2).map { it.inspected }.reduce(Long::times)
  }

  private fun parseInput(input: List<String>): List<Monkey> {
    return mutableListOf<Monkey>().apply {
      input.chunked(7).forEach {
        add(parseMonkey(it))
      }
    }
  }

  private fun parseMonkey(input: List<String>): Monkey {
    val worry = input[1].substring("  Starting items: ".length).split(", ").map { Integer.parseInt(it).toLong() }
    val ops = input[2].substring("  Operation: new = ".length).split(" ")
    val throwMod = input[3].substring("  Test: divisible by ".length).let { Integer.parseInt(it).toLong() }
    val truthy = input[4].substring("    If true: throw to monkey ".length).let { Integer.parseInt(it) }
    val falsy = input[5].substring("    If false: throw to monkey ".length).let { Integer.parseInt(it) }
    val items = LinkedList<Item>().apply { worry.map { Item(it) }.forEach { add(it) } }
    val operation = parseOperation(ops)
    val throwTo = { item: Item, mod: Long -> if (item.worry % mod == 0L) { truthy } else { falsy } }
    return Monkey(items, operation, throwTo, throwMod)
  }

  private fun parseOperation(ops: List<String>): (Item) -> Long {
    val scalar = ops[2].toLongOrNull()
    return when (ops[1]) {
      "+" -> { item: Item -> item.worry + (scalar ?: item.worry) }
      "*" -> { item: Item -> item.worry * (scalar ?: item.worry) }
      else -> throw AssertionError()
    }
  }

  private class Monkey(
    val items: LinkedList<Item>,
    private val worryFn: (Item) -> Long,
    private val throwTo: (Item, Long) -> Int,
    val throwMod: Long
  ) {
    companion object {
      var maxWorry by Delegates.notNull<Long>()
    }

    private val origItems = LinkedList<Item>().apply {
      items.forEach {
        add(it)
      }
    }
    var adjustWorry: (Item) -> Long = { it.worry }
    val hasItems
      get() = items.size != 0
    var inspected = 0L
      private set

    fun reset() {
      inspected = 0
      while (hasItems) {
        items.poll()
      }
      origItems.forEach {
        items.add(it)
        it.reset()
      }
    }

    fun inspect(): Pair<Int, Item> {
      if (items.size == 0) {
        throw AssertionError()
      }
      inspected++
      val item = items.poll()
      item.worry = worryFn(item)
      item.worry = adjustWorry(item)
      item.worry = item.worry % maxWorry
      return Pair(throwTo(item, throwMod), item)
    }

    override fun toString(): String {
      return "Monkey(Items=$items,Inspected=$inspected)"
    }
  }

  private data class Item(var worry: Long) {
    val origWorry = worry
    fun reset() {
      worry = origWorry
    }
  }
}
