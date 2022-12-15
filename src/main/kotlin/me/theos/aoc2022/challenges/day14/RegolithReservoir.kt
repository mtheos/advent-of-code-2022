package me.theos.aoc2022.challenges.day14

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.awt.Point
import java.lang.AssertionError

class RegolithReservoir : Challenge {
  private val cave: Cave by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    cave.fillWithTheSandsOfTime()
//    cave.draw()
    val sands = cave.filter { it.value == Tile.Sand }.count()
    return "Part 1: $sands"
  }

  override fun solveHard(): String {
    cave.letTheSandsOfTimeHitTheFloor()
//    cave.draw()
    val sands = cave.filter { it.value == Tile.Sand }.count()
    return "Part 2: $sands"
  }

  private fun parseInput(input: List<String>): Cave {
    return Cave().apply {
      input.forEach { line ->
        val points = line.split(" -> ").map { p ->
          p.split(",").map(Integer::parseInt).chunked(2).map { Point(it[0], it[1]) }.first()
        }
        for (i in 0 until points.size - 1) {
          val (first, second) = orientPoints(points[i], points[i + 1])
          when {
            first.x < second.x -> {
              repeat(second.x - first.x + 1) {
                this[Point(first.x + it, first.y)] = Tile.Wall
              }
            }

            else -> {
              repeat(second.y - first.y + 1) {
                this[Point(first.x, first.y + it)] = Tile.Wall
              }
            }
          }
        }
      }
    }
  }

  private fun orientPoints(first: Point, second: Point): Pair<Point, Point> {
    return when {
      first.x < second.x -> Pair(first, second)
      first.x > second.x -> Pair(second, first)
      first.y < second.y -> Pair(first, second)
      first.y > second.y -> Pair(second, first)
      else -> throw AssertionError()
    }
  }

  private class Cave : HashMap<Point, Tile>() {
    private val theAbyssAkaTheFloor: Int by lazy {
      maxOf { it.key.y + 2 }
    }

    companion object {
      private val source = Point(500, 0)
    }

    operator fun get(x: Int, y: Int): Tile {
      return this[Point(x, y)]
    }

    override operator fun get(key: Point): Tile {
      return super.get(key) ?: Tile.Air
    }

    operator fun set(key: Point, value: Tile) {
      put(key, value)
    }

    override fun put(key: Point, value: Tile): Tile? {
      if (this[key] != Tile.Air && this[key] != value) {
        throw AssertionError()
      }
      return super.put(key, value)
    }

    fun fillWithTheSandsOfTime() {
      var grain = Point(source.x, source.y)
      while (grain.y < theAbyssAkaTheFloor) {
        val next = fall(grain)
        if (next == grain) {
          this[next] = Tile.Sand
          grain = Point(source.x, source.y)
        } else {
          grain = next
        }
      }
    }

    fun letTheSandsOfTimeHitTheFloor() {
      var grain = Point(source.x, source.y)
      while (true) {
        val next = fall(grain)
        if (next == source) {
          this[next] = Tile.Sand
          break
        }
        if (next == grain) {
          this[next] = Tile.Sand
          grain = Point(source.x, source.y)
        } else if (next.y + 1 == theAbyssAkaTheFloor) {
          this[next] = Tile.Sand
          grain = Point(source.x, source.y)
        } else {
          grain = next
        }
      }
    }

    private fun fall(grain: Point): Point {
      return when (Tile.Air) {
        this[grain.x, grain.y + 1] -> grain.location.apply { translate(0, 1) }
        this[grain.x - 1, grain.y + 1] -> grain.location.apply { translate(-1, 1) }
        this[grain.x + 1, grain.y + 1] -> grain.location.apply { translate(1, 1) }
        else -> grain.location
      }
    }

    fun draw() {
      val minX = minOf { it.key.x }
      val maxX = maxOf { it.key.x }
      val minY = minOf { it.key.y }
      val maxY = maxOf { it.key.y }
      repeat(maxY - minY + 1) { y ->
        repeat(maxX - minX + 1) { x ->
          print(this[Point(minX + x, minY + y)].repr)
        }
        println()
      }
    }
  }

  private enum class Tile(val repr: String) {
    Air("  "),
    Sand("x "),
    Wall("█ ")
  }
}
