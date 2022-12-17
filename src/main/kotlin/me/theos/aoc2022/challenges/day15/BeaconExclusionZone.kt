package me.theos.aoc2022.challenges.day15

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.awt.Point
import java.lang.AssertionError
import java.util.regex.Pattern
import kotlin.math.abs

class BeaconExclusionZone : Challenge {
  private val sensors: List<Point>
  private val beacons: List<Point>

  init {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input).run {
      sensors = first
      beacons = second
    }
  }

  override fun solveEasy(): String {
    val targetRow = 2000000
    val empty = mutableSetOf<Point>()
    sensors.zip(beacons).filter {
      manhattan(it.first, it.second) >= it.first.y - targetRow
    }.forEach { it ->
      val s = it.first
      val b = it.second
      val distance = abs(s.y - targetRow)
      val left = s.x - manhattan(s, b) + distance
      val right = s.x + manhattan(s, b) - distance
      (left..right).forEach { x ->
        if (beacons.none { it.x == x && it.y == targetRow }) {
          empty.add(Point(x, targetRow))
        }
      }
    }
    return "Part 1: ${empty.count()}"
  }

  override fun solveHard(): String {
    val scalar = 4_000_000
    val maxDims = 4_000_000
    val signal = findSignal(maxDims)
    return "Part 2: ${(signal.x.toLong() * scalar) + signal.y}"
  }

  private fun findSignal(maxDims: Int): Point {
    for (it in sensors.zip(beacons)) {
      val s = it.first
      val b = it.second
      val topY = s.y - manhattan(s, b) - 1
      val bottomY = s.y + manhattan(s, b) + 1
      for (y in topY..bottomY) {
        val distanceToCenter = abs(s.y - y)
        val leftX = s.x - manhattan(s, b) - 1 + distanceToCenter
        val rightX = s.x + manhattan(s, b) + 1 - distanceToCenter
        val left = Point(leftX, y)
        val right = Point(rightX, y)
        if (leftX < 0 || leftX > maxDims || rightX < 0 || rightX > maxDims || y < 0 || y > maxDims) {
          continue
        }
        if (test(left)) {
          return left
        } else if (test(right)) {
          return right
        }
      }
    }
    throw AssertionError()
  }

  private fun test(p: Point): Boolean {
    return sensors.zip(beacons).none {
      val s = it.first
      val b = it.second
      manhattan(s, b) >= manhattan(s, p)
    }
  }

  private fun manhattan(sensor: Point, beacon: Point): Int =
    abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)

  private fun parseInput(input: List<String>): Pair<List<Point>, List<Point>> {
    val sensors = mutableListOf<Point>()
    val beacons = mutableListOf<Point>()
    val pattern = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    input.forEach {
      val m = pattern.matcher(it)
      when (m.matches()) {
        true -> {
          val sx = m.group(1).toInt()
          val sy = m.group(2).toInt()
          val bx = m.group(3).toInt()
          val by = m.group(4).toInt()
          sensors.add(Point(sx, sy))
          beacons.add(Point(bx, by))
        }
        false -> throw AssertionError()
      }
    }
    return Pair(sensors, beacons)
  }
}
