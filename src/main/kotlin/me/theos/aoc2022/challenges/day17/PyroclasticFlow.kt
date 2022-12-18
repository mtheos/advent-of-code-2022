package me.theos.aoc2022.challenges.day17

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.lang.AssertionError
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PyroclasticFlow : Challenge {
  private val currents: List<AirFlow> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val highestPoint = Chamber(currents).`nowYou'reThinkingInCycles`(2022)
    return "Part 1: $highestPoint"
  }

  override fun solveHard(): String {
    val highestPoint = Chamber(currents).`nowYou'reThinkingInCycles`(1_000_000_000_000)
    return "Part 2: $highestPoint"
  }

  private fun parseInput(input: List<String>): List<AirFlow> {
    return input.first().map {
      when (it) {
        '<' -> AirFlow.Left
        '>' -> AirFlow.Right
        else -> throw AssertionError()
      }
    }
  }

  private data class Point(var x: Int, var y: Long) {
    val location
      get() = Point(x, y)
    constructor(x: Int, y: Int) : this(x, y.toLong())
    fun translate(x: Int, y: Int): Point {
      return translate(x, y.toLong())
    }
    fun translate(x: Int, y: Long): Point {
      this.x += x
      this.y += y
      return this
    }
  }

  private class Chamber(val currents: List<AirFlow>) : HashMap<Point, Rock>() {
    private var currentsIdx = 0
    private var shapesIdx = 0
    private val current = sequence {
      var idx = 0
      while (true) {
        yield(currents[idx])
        idx = (idx + 1) % currents.size
      }
    }.iterator()
    private val shapes = sequence {
      val order = listOf(Shape.Horizontal, Shape.Cross, Shape.Bend, Shape.Vertical, Shape.Square)
      var idx = 0
      while (true) {
        yield(order[idx])
        idx = (idx + 1) % order.size
      }
    }.iterator()
    init {
      repeat(chamberWidth) {
        this[Point(it, 0)] = Rock(Shape.Spot, Point(it, 0))
      }
    }
    companion object {
      private const val chamberWidth = 7
      private const val newBlockXOffset = 2
      private const val newBlockYOffset = 4
    }
    operator fun get(x: Int, y: Int): Rock? {
      return this[Point(x, y)]
    }
    override operator fun get(key: Point): Rock? {
      return super.get(key)
    }
    operator fun set(key: Point, value: Rock) {
      put(key, value)
    }
    override fun put(key: Point, value: Rock): Rock? {
      if (this[key] != null && this[key] != value) {
        throw AssertionError()
      }
      return super.put(key, value)
    }

    // Good naming vs proper punctuation...it was never good naming to begin with
    fun `nowYou'reThinkingInCycles`(droppedRocks: Long): Long {
      // Sim at most 5000 rocks and get the height changes
      val heightChange = sim5000Rocks()
      val (preamble, cycle) = findCycle(heightChange)
      val cycles = if (droppedRocks % cycle.size < preamble.size) {
        droppedRocks / cycle.size - 1
      } else {
        droppedRocks / cycle.size
      }
      val residual = (droppedRocks - (cycles * cycle.size) - preamble.size).toInt()
      return preamble.sum() + cycle.sum() * cycles + cycle.take(residual).sum()
    }

    private fun sim5000Rocks(): List<Int> {
      var highestPoint = 0L
      val heightChange = mutableListOf<Int>()
      // double the work for part 1 /shrug
      var count = 5000
      var rock = Rock(shapes.next(), Point(newBlockXOffset, highestPoint + newBlockYOffset))
      while (count-- > 0) {
        dropAndWeave(rock)
        emplace(rock)
        if (rock.top > highestPoint) {
          heightChange.add((rock.top - highestPoint).toInt())
          highestPoint = rock.top
        } else {
          heightChange.add(0)
        }
        rock = Rock(shapes.next(), Point(newBlockXOffset, highestPoint + newBlockYOffset))
      }
      return heightChange
    }

    // assume min cycle length of 10 to avoid short string matches
    // find the smallest substring whose next occurrence is idx + len + 1
    // ???
    // profit
    private fun findCycle(heightChange: List<Int>): Pair<List<Int>, List<Int>> {
      val minCycle = 10
      val heights = heightChange.fold("") { acc, it -> acc + it }
      var cycleLen = minCycle
      var idx = 0
      while (true) {
        val maybeCycle = heights.substring(idx, idx + cycleLen)
        if (heights.substring(idx + cycleLen).indexOf(maybeCycle) == 0) {
          val preamble = heights.substring(0, idx).asIterable().map { it.digitToInt() }
          val cycle = maybeCycle.asIterable().map { it.digitToInt() }
          return Pair(preamble, cycle)
        } else if (heights.substring(idx + cycleLen).contains(maybeCycle)) {
          cycleLen++
        } else {
          cycleLen = minCycle
          idx++
        }
      }
    }

    fun emplace(rock: Rock) {
      rock.occupies().forEach { this[it] = rock }
    }

    private fun dropAndWeave(rock: Rock) {
      while (true) {
//        draw(rock)
        when (current.next()) {
          AirFlow.Left -> {
            if (rock.occupies().all { this[it.translate(-1, 0)] == null }) {
              rock.pos.x = max(rock.pos.x - 1, 0)
            }
          }
          AirFlow.Right -> {
            if (rock.occupies().all { this[it.translate(1, 0)] == null }) {
              rock.pos.x = min(rock.pos.x + 1, chamberWidth)
              if (rock.right >= chamberWidth) {
                rock.pos.x = chamberWidth - rock.shape.width
              }
            }
          }
        }
//        draw(rock)
        if (rock.occupies().all {
              this[it.translate(0, -1)] == null
        }) {
          rock.pos.y--
        } else {
          return
        }
      }
    }

    fun draw(falling: Rock) {
      println("+--------------+")
      for (y in falling.top downTo 1) {
        print("|")
        repeat(7) { x ->
          val ch = if (this[Point(x, y)] != null) {
            "# "
          } else if (falling.occupies().any { it == Point(x, y)}) {
            "@ "
          } else {
            ". "
          }
          print(ch)
        }
        println("|")
      }
      println("+--------------+")
    }
  }

  private class Rock(val shape: Shape, val pos: Point) {
    val top
      get() = pos.y + shape.height - 1
    val bottom
      get() = pos.y
    val left
      get() = pos.x
    val right
      get() = pos.x + shape.width - 1
    fun collidesWith(other: Rock): Boolean {
      if (abs(pos.x - other.pos.x) > 4
          || abs(pos.y - other.pos.y) > 4) {
        return false
      }
      occupies().forEach { first ->
        other.occupies().forEach { second ->
          if (first == second) {
            return true
          }
        }
      }
      return false
    }
    fun occupies(): List<Point> {
      return shape.points().map {
        pos.location.translate(it.x, it.y)
      }
    }
    override fun toString(): String {
      return "Rock{shape=${shape.name}, pos=${pos}}"
    }
  }
  private enum class Shape(val height: Int, val width: Int) {
    Spot(1, 1),
    Horizontal(1, 4),
    Cross(3, 3),
    Bend(3, 3),
    Vertical(4, 1),
    Square(2, 2);
    fun points(): List<Point> {
      return when (this) {
        Spot -> {
          listOf(Point(0, 0))
        }
        Horizontal -> {
          listOf(
            Point(0, 0),
            Point(1, 0),
            Point(2, 0),
            Point(3, 0)
          )
        }
        Cross -> {
          listOf(
            Point(1, 0),
            Point(0, 1),
            Point(1, 1),
            Point(2, 1),
            Point(1, 2)
          )
        }
        Bend -> {
          listOf(
            Point(0, 0),
            Point(1, 0),
            Point(2, 0),
            Point(2, 1),
            Point(2, 2)
          )
        }
        Vertical -> {
          listOf(
            Point(0, 0),
            Point(0, 1),
            Point(0, 2),
            Point(0, 3)
          )
        }
        Square -> {
          listOf(
            Point(0, 0),
            Point(1, 0),
            Point(0, 1),
            Point(1, 1)
          )
        }
      }
    }
  }
  private enum class AirFlow {
    Left,
    Right;
  }
}
