package me.theos.aoc2022.challenges.day12

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
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
    val startingPoints = mutableListOf<Pair<Int, Int>>().apply {
      mountain.terrain.chunked(mountain.terrain.dimX).withIndex().forEach { line ->
        line.value.withIndex().forEach {
          if (it.value == 0) {
            add(Pair(line.index, it.index))
          }
        }
      }
    }
    val steps = startingPoints.map { bfs(it, mountain.end) }.filter { it > 0 }.min()
    return "Part 2: $steps"
  }

  private fun bfs(start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
    val dirs = listOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))
    val steps = List2D<Int>(mountain.terrain.dimY, mountain.terrain.dimX).init(Int.MAX_VALUE)
      .also { it[start] = 0 }
    val visited = List2D<Boolean>(mountain.terrain.dimY, mountain.terrain.dimX).init(false)
    val q = PriorityQueue<Pair<Int, Int>>(Comparator { o1, o2 -> steps[o1] - steps[o2] })
      .also { it.add(start) }

    while (q.size > 0) {
      val cur = q.poll()
      visited[cur] = true
      if (cur == end) {
        return steps[cur]
      }
      dirs.forEach { dir ->
        val next = Pair(cur.first + dir.first, cur.second + dir.second)
        if (next.first >= 0 && next.first < mountain.terrain.dimY
            && next.second >= 0 && next.second < mountain.terrain.dimX) {
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
    var start: Pair<Int, Int>? = null
    var end: Pair<Int, Int>? = null
    val dimY = input.size
    val dimX = input.first().length
    val terrain = mutableListOf<Int>().apply {
      input.withIndex().forEach {line ->
        line.value.asIterable().withIndex().map { point ->
          add(when (point.value) {
            'S' -> {
              start = Pair(line.index, point.index)
              0
            }
            'E' -> {
              end = Pair(line.index, point.index)
              'z' - 'a'
            }
            else -> point.value - 'a'
          })
        }
      }
    }
    return Mountain(List2D.fromList(terrain, dimY, dimX), start!!, end!!)
  }

  private class Mountain(val terrain: List2D<Int>, val start: Pair<Int, Int>, val end: Pair<Int, Int>) {
    operator fun get(p: Pair<Int, Int>): Int = terrain[p]
    operator fun get(y: Int, x: Int): Int = terrain[y, x]
  }

  private class List2D<T>(val dimY: Int, val dimX: Int) : ArrayList<T?>() {
    companion object {
      fun <T> fromList(input: List<T>, dimY: Int, dimX: Int): List2D<T> {
        return List2D<T>(dimY, dimX).apply {
          input.chunked(dimX).withIndex().forEach { row ->
            row.value.withIndex().forEach {
              this[row.index, it.index] = it.value
            }
          }
        }
      }
    }

    init { // more useful as a 2d array, so null everything to set the bounds
      repeat(dimY) {
        repeat(dimX) {
          add(null)
        }
      }
    }

    fun init(v: T): List2D<T> { // ...even better without NPEs
      repeat(dimY) {y ->
        repeat(dimX) {x ->
          this[y, x] = v
        }
      }
      return this
    }

    operator fun get(p: Pair<Int, Int>): T {
      return this[p.first, p.second]
    }
    operator fun get(y: Int, x: Int): T {
      checkBounds(y, x)
      return this[y * dimX + x]!!
    }
    operator fun set(p: Pair<Int, Int>, v: T) {
      this[p.first, p.second] = v
    }
    operator fun set(y: Int, x: Int, v: T) {
      checkBounds(y, x)
      this[y * dimX + x] = v
    }

    private fun checkBounds(y: Int, x: Int) {
      if (y * dimX > size) {
        throw ArrayIndexOutOfBoundsException("$y is out of bounds for size $size")
      }
      if (y * dimX + x > size) {
        throw ArrayIndexOutOfBoundsException("($y, $x) is out of bounds for size $size. Attempted to access idx ${y * dimX + x}. Dims ($dimY, $dimX).")
      }
    }
  }
}
