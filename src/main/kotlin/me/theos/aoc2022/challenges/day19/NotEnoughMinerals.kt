package me.theos.aoc2022.challenges.day19

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import me.theos.aoc2022.challenges.day19.NotEnoughMinerals.OreType.Clay
import me.theos.aoc2022.challenges.day19.NotEnoughMinerals.OreType.Geode
import me.theos.aoc2022.challenges.day19.NotEnoughMinerals.OreType.Obsidian
import me.theos.aoc2022.challenges.day19.NotEnoughMinerals.OreType.Ore
import java.util.*
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.max

class NotEnoughMinerals : Challenge {
  private val collections: List<Map<OreType, Blueprint>> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val geodes = collections.map {
      sim(it, 24)
    }
    val totalQuality = geodes.withIndex().map { (it.index + 1) * it.value }.reduce { acc, it -> acc + it }
    return "Part 1: $totalQuality"
  }

  override fun solveHard(): String {
    val geodes = collections.take(3).map {
      sim(it, 32)
    }
    val totalQuality = geodes.reduce { acc, it -> acc * it }
    return "Part 2: $totalQuality"
  }

  private fun sim(blueprints: Map<OreType, Blueprint>, maxTime: Int): Int {
    val start = State.empty().copy(time = 1).apply { robots.oreMiner++ }
    val queue = LinkedList<State>()
    val nodes = LinkedHashSet<State>()
    queue.add(start)
    nodes.add(start)
    val edges = mutableMapOf<State, MutableMap<State, Int>>()
    val resourceMax = Resources(
      blueprints.values.maxOf { it.costs.ore },
      blueprints.values.maxOf { it.costs.clay },
      blueprints.values.maxOf { it.costs.obsidian },
      Int.MAX_VALUE
    )
    val botMax = Robots(
      blueprints.values.maxOf { it.costs.ore },
      blueprints.values.maxOf { it.costs.clay },
      blueprints.values.maxOf { it.costs.obsidian },
      Int.MAX_VALUE
    )
    while (!queue.isEmpty()) {
      val current = queue.remove()
      val currTime = current.time
      edges.putIfAbsent(current, mutableMapOf())
      val outEdges = edges[current]!!
      if (currTime < maxTime) {
        for (blueprint in blueprints.values) {
          val robot = blueprint.robot
          if (current.robots[robot] >= botMax[robot]) {
            continue
          } else if (current.resources[robot] > resourceMax[robot]) {
            continue
          } else if (!blueprint.canBuildEventually(current)) {
            continue
          }

          val buildTime = blueprint.buildTime(current)
          val timeAtBuild = currTime + buildTime
          if (timeAtBuild > maxTime) {
            continue
          }

          val next = State(current, timeAtBuild)
          next.resources.ore += next.robots.oreMiner * buildTime
          next.resources.clay += next.robots.clayMiner * buildTime
          next.resources.obsidian += next.robots.obsidianMiner * buildTime

          blueprint.build(next)
          next.robots[blueprint.robot]++

          if (!nodes.contains(next)) {
            nodes.add(next)
            queue.add(next)
          }
          outEdges[next] = if (blueprint.robot == Geode) maxTime - timeAtBuild + 1 else 0
        }
      }
    }
    val total = HashMap<State, Int>()
    total[start] = 0
    for (node in nodes) {
      val nodeTotal = total[node]!!
      val outEdges = edges[node]!!
      for (edge in outEdges) {
        val weight = edge.value
        val newTotal = nodeTotal + weight
        total.compute(edge.key) { _, v -> if (v == null) newTotal else max(v, newTotal) }
      }
    }
    return total.values.max()
  }

  private fun parseInput(input: List<String>): List<Map<OreType, Blueprint>> {
    val orePattern = Pattern.compile(".*?(\\d+) ore.*?")
    val clayPattern = Pattern.compile(".*?(\\d+) clay.*?")
    val obsidianPattern = Pattern.compile(".*?(\\d+) obsidian.*?")
    val collections = input.map { line ->
      val robots = line.split(" Each ").dropWhile { it.startsWith("Blueprint") }
      val blueprints = robots.map {
        val oreType = OreType.from(it.substringBefore(' '))
        val oreCost = orePattern.matcher(it).let { m -> if (m.matches()) m.group(1).toInt() else 0 }
        val clayCost = clayPattern.matcher(it).let { m -> if (m.matches()) m.group(1).toInt() else 0 }
        val obsidianCost = obsidianPattern.matcher(it).let { m -> if (m.matches()) m.group(1).toInt() else 0 }
        Blueprint(oreType, Resources(oreCost, clayCost, obsidianCost, 0))
      }.associateBy { it.robot }
      blueprints
    }
    return collections
  }

  private data class Blueprint(val robot: OreType, val costs: Resources) {
    fun canBuildEventually(state: State): Boolean {
      return (costs.ore == 0 || state.robots.oreMiner > 0)
          && (costs.clay == 0 || state.robots.clayMiner > 0)
          && (costs.obsidian == 0 || state.robots.obsidianMiner > 0)
    }

    fun build(state: State) {
      state.resources.ore -= costs.ore
      state.resources.clay -= costs.clay
      state.resources.obsidian -= costs.obsidian
    }

    fun buildTime(state: State): Int {
      val missingOre = costs.ore - state.resources.ore
      val missingClay = costs.clay - state.resources.clay
      val missingObsidian = costs.obsidian - state.resources.obsidian
      val timeOre = if (missingOre > 0) {
        ceil(missingOre.toDouble() / state.robots.oreMiner).toInt() + 1
      } else {
        1
      }
      val timeClay = if (missingClay > 0) {
        ceil(missingClay.toDouble() / state.robots.clayMiner).toInt() + 1
      } else {
        1
      }
      val timeObsidian = if (missingObsidian > 0) {
        ceil(missingObsidian.toDouble() / state.robots.obsidianMiner).toInt() + 1
      } else {
        1
      }
      return max(timeOre, max(timeClay, timeObsidian))
    }
  }

  private data class Resources(var ore: Int, var clay: Int, var obsidian: Int, var geode: Int) {
    constructor(r: Resources) : this(r.ore, r.clay, r.obsidian, r.geode)
    companion object {
      fun empty(): Resources {
        return Resources(0, 0, 0, 0)
      }
    }
    operator fun get(r: OreType): Int {
      return when (r) {
        Ore -> ore
        Clay -> clay
        Obsidian -> obsidian
        Geode -> geode
      }
    }
    operator fun set(r: OreType, v: Int) {
      when (r) {
        Ore -> ore = v
        Clay -> clay = v
        Obsidian -> obsidian = v
        Geode -> geode = v
      }
    }
  }

  private data class Robots(var oreMiner: Int, var clayMiner: Int, var obsidianMiner: Int, var geodeMiner: Int) {
    constructor(r: Robots) : this(r.oreMiner, r.clayMiner, r.obsidianMiner, r.geodeMiner)
    companion object {
      fun empty(): Robots {
        return Robots(0, 0, 0, 0)
      }
    }
    operator fun get(r: OreType): Int {
      return when (r) {
        Ore -> oreMiner
        Clay -> clayMiner
        Obsidian -> obsidianMiner
        Geode -> geodeMiner
      }
    }
    operator fun set(r: OreType, v: Int) {
      when (r) {
        Ore -> oreMiner = v
        Clay -> clayMiner = v
        Obsidian -> obsidianMiner = v
        Geode -> geodeMiner = v
      }
    }
  }

  private data class State(val robots: Robots, val resources: Resources, val time: Int) {
    constructor(state: State, time: Int) : this(Robots(state.robots), Resources(state.resources), time)
    companion object {
      fun empty(): State {
        return State(Robots.empty(), Resources.empty(), 0)
      }
    }
  }

  private enum class OreType {
    Ore,
    Clay,
    Obsidian,
    Geode;

    companion object {
      fun from(s: String): OreType {
        return when (s) {
          "ore" -> Ore
          "clay" -> Clay
          "obsidian" -> Obsidian
          "geode" -> Geode
          else -> throw AssertionError()
        }
      }
    }
  }
}
