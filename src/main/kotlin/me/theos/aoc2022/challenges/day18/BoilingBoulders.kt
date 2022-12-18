package me.theos.aoc2022.challenges.day18

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.util.LinkedList
import kotlin.math.abs

class BoilingBoulders : Challenge {
  private val cubes: List<Point3D> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    return "Part 1: ${exposedSides()}"
  }

  override fun solveHard(): String {
    val pockets = mutableListOf<Point3D>().apply {
      val maxX = cubes.maxOf { it.x }
      val maxY = cubes.maxOf { it.y }
      val maxZ = cubes.maxOf { it.z }
      repeat(maxX) { x ->
        repeat(maxY) { y ->
          repeat(maxZ) { z ->
            addPockets(Point3D(x, y, z), this, Point3D(maxX, maxY, maxZ))
          }
        }
      }
    }
    return "Part 2: ${exposedSides(pockets)}"
  }

  private fun exposedSides(pockets: List<Point3D> = listOf()): Int {
    var exposed = cubes.count() * 6
    for (i in cubes.indices) {
      for (j in i + 1 until cubes.size) {
        val c1 = cubes[i]
        val c2 = cubes[j]
        if (c1.touches(c2)) {
          exposed -= 2
        }
      }
    }
    for (i in cubes.indices) {
      for (j in pockets.indices) {
        val c1 = cubes[i]
        val c2 = pockets[j]
        if (c1.touches(c2)) {
          exposed -= 1
        }
      }
    }
    return exposed
  }

  private fun addPockets(point: Point3D, pockets: MutableList<Point3D>, bounds: Point3D) {
    if (cubes.contains(point) || pockets.contains(point)) {
      return
    }
    val q = LinkedList<Point3D>()
    val seen = mutableSetOf<Point3D>()
    seen.add(point)
    q.add(point)
    while (q.isNotEmpty()) {
      val p = q.poll()
      if ((p.x < 0 || p.x > bounds.x)
          || (p.y < 0 || p.y > bounds.y)
          || (p.z < 0 || p.z > bounds.z)) {
        return
      }
      listOf(
        Point3D(p.x + 1, p.y, p.z), Point3D(p.x - 1, p.y, p.z),
        Point3D(p.x, p.y + 1, p.z), Point3D(p.x, p.y - 1, p.z),
        Point3D(p.x, p.y, p.z + 1), Point3D(p.x, p.y, p.z - 1)
      ).forEach {
        if (!seen.contains(it) && !cubes.contains(it)) {
          q.add(it)
          seen.add(it)
        }
      }
    }
    // Anything we've seen that isn't a cube is also a pocket
    pockets.addAll(seen.filter { !cubes.contains(it) })
  }

  private fun parseInput(input: List<String>): List<Point3D> {
    return input.map { line ->
      val (x, y, z) = line.split(",", limit = 3).map { it.toInt() }
      Point3D(x, y, z)
    }
  }

  private data class Point3D(var x: Int, var y: Int, var z: Int) {
    val location
      get() = Point3D(x, y, z)

    private fun manhattanDistance(other: Point3D): Int = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    fun touches(other: Point3D): Boolean {
      if (this == other) {
        return false // Contrary to popular belief, this problem is no fun when you touch yourself
      }
      if (manhattanDistance(other) > 1) {
        return false
      }
      // If 2 coords are the same and the 3rd is different the cubes are adjacent
      if (x == other.x && y == other.y && z != other.z
          || x == other.x && y != other.y && z == other.z
          || x != other.x && y == other.y && z == other.z) {
        return true
      }
      return false
    }
  }
}
