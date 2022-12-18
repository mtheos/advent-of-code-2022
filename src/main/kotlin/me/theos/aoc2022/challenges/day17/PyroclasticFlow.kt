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
    val highestPoint = Chamber(currents).popLockAndRocKIt(2022)
    return "Part 1: $highestPoint"
  }

  override fun solveHard(): String {
    val highestPoint = Chamber(currents).popLockAndRocKIt(1_000_000_000_000)
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
    private var lowestPoint = 0L
    private var highestPoint = 0L
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
    fun popLockAndRocKIt(droppedRocks: Long): Long {
      var count = droppedRocks
      var rock = Rock(shapes.next(), Point(newBlockXOffset, highestPoint + newBlockYOffset))
      while (count-- > 0) {
        if (count > 0 && count % 100_000 == 0L) {
          cull()
        }
        dropAndWeave(rock)
        emplace(rock)
        if (rock.top > highestPoint) {
          highestPoint = rock.top
        }
        rock = Rock(shapes.next(), Point(newBlockXOffset, highestPoint + newBlockYOffset))
      }
      return highestPoint
    }

    private fun cull() {
      var y = highestPoint
      var cull = false
      var newLowest = lowestPoint
      while (y >= lowestPoint) {
        val row = (0..6).map { Point(it, y) }
        if (!cull && row.all { this[it] != null }) {
          cull = true
          newLowest = y
          y--
          continue
        }
        if (cull) {
          row.forEach { this.remove(it) }
        }
        y--
      }
      lowestPoint = newLowest
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
      for (y in max(highestPoint + newBlockYOffset, falling.top) downTo 1) {
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
