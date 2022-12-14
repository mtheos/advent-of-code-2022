package me.theos.aoc2022.challenges.day12

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.awt.Point
import java.util.PriorityQueue

class HillClimbingAlgorithm : Challenge {
  private val mountain: Mountain by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val steps = bfs(mountain.start, mountain.end)
    return "Part 1: $steps"
  }

  override fun solveHard(): String {
    val startingPoints = mutableListOf<Point>().apply {
      mountain.terrain.chunked(mountain.terrain.dimX).withIndex().forEach { line ->
        line.value.withIndex().forEach {
          if (it.value == 0) {
            add(Point(it.index, line.index))
          }
        }
      }
    }
    val steps = startingPoints.map { bfs(it, mountain.end) }.filter { it > 0 }.min()
    return "Part 2: $steps"
  }

  private fun bfs(start: Point, end: Point): Int {
    val dirs = listOf(Point(0, 1), Point(1, 0), Point(0, -1), Point(-1, 0))
    val steps = List2D(mountain.terrain.dimY, mountain.terrain.dimX, Int.MAX_VALUE).also { it[start] = 0 }
    val visited = List2D(mountain.terrain.dimY, mountain.terrain.dimX, false)
    val q = PriorityQueue<Point> { o1, o2 -> steps[o1] - steps[o2] }
      .also { it.add(start) }

    while (q.size > 0) {
      val cur = q.poll()
      visited[cur] = true
      if (cur == end) {
        return steps[cur]
      }
      dirs.forEach { dir ->
        val next = Point(cur.x + dir.x, cur.y + dir.y)
        if (next.y >= 0 && next.y < mountain.terrain.dimY
            && next.x >= 0 && next.x < mountain.terrain.dimX) {
          if (!visited[next]) {
            if (mountain.terrain[next] - mountain.terrain[cur] <= 1) {
              val nextSteps = steps[cur] + 1
              if (nextSteps < steps[next]) {
                q.add(next)
                steps[next] = nextSteps
              }
            }
          }
        }
      }
    }
    return -1
  }

  private fun parseInput(input: List<String>): Mountain {
    var start: Point? = null
    var end: Point? = null
    val dimY = input.size
    val dimX = input.first().length
    val terrain = mutableListOf<Int>().apply {
      input.withIndex().forEach {line ->
        line.value.asIterable().withIndex().map { point ->
          add(when (point.value) {
            'S' -> {
              start = Point(point.index, line.index)
              0
            }
            'E' -> {
              end = Point(point.index, line.index)
              'z' - 'a'
            }
            else -> point.value - 'a'
          })
        }
      }
    }
    return Mountain(List2D.fromList(terrain, dimY, dimX), start!!, end!!)
  }

  private class Mountain(val terrain: List2D<Int>, val start: Point, val end: Point) {
    operator fun get(p: Point): Int = terrain[p]
    operator fun get(y: Int, x: Int): Int = terrain[y, x]
  }

  private class List2D<T>(val dimY: Int, val dimX: Int, fillWith: T) : ArrayList<T>() {
    companion object {
      fun <T> fromList(input: List<T>, dimY: Int, dimX: Int): List2D<T> {
        return List2D(dimY, dimX, input[0]).apply {
          input.chunked(dimX).withIndex().forEach { row ->
            row.value.withIndex().forEach {
              this[row.index, it.index] = it.value
            }
          }
        }
      }
    }

    init {
      repeat(dimY) {
        repeat(dimX) {
          add(fillWith)
        }
      }
    }

    operator fun get(p: Point): T {
      return this[p.y, p.x]
    }
    operator fun get(y: Int, x: Int): T {
      checkBounds(y, x)
      return this[y * dimX + x]
    }
    operator fun set(p: Point, v: T) {
      this[p.y, p.x] = v
    }
    operator fun set(y: Int, x: Int, v: T) {
      checkBounds(y, x)
      this[y * dimX + x] = v
    }

    private fun checkBounds(y: Int, x: Int) {
      if (y * dimX >= size) {
        throw ArrayIndexOutOfBoundsException("$y is out of bounds for size $size")
      }
      if (y * dimX + x >= size) {
        throw ArrayIndexOutOfBoundsException("($y, $x) is out of bounds for size $size. Attempted to access idx ${y * dimX + x}. Dims ($dimY, $dimX).")
      }
    }
  }
}
