package me.theos.aoc2022.challenges.day07

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils

class NoSpaceLeftOnDevice : Challenge {
  private val fsRoot: AoCDirectory by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val sizeLt100000 = fsRoot.dirSizes().values.filter { it < 100_000 }.sum()
    return "Part 1: $sizeLt100000"
  }

  override fun solveHard(): String {
    val totalSize = 70_000_000
    val totalRequired = 30_000_000
    val currentFree = totalSize - fsRoot.size()
    val required = totalRequired - currentFree
    val bigEnough = fsRoot.dirSizes().filter { it.value >= required }
    val smallestPossible = bigEnough.values.min()
    return "Part 2: $smallestPossible"
  }

  private fun parseInput(input: List<String>): AoCDirectory {
    val root = AoCDirectory("/", null)
    var currentDir = root
    input.iterator().forEach { next ->
      when {
        next.startsWith("\$ cd") -> {
          val dir = next.substringAfter("\$ cd ")
          currentDir = if (dir == "..") {
            currentDir.parent ?: currentDir
          } else if (currentDir.children.any { it.name == dir }) {
            currentDir.children.first { it.name == dir }
          } else if (currentDir.name == "/" && currentDir.name == dir) {
            currentDir
          } else {
            throw AssertionError()
          }
        }
        next.startsWith("\$ ls") -> {
          // no-op next lines will contain files
        }
        next.startsWith("dir") -> {
          val dir = next.substringAfter("dir ")
          if (!currentDir.children.any { it.name == dir }) {
            currentDir.children.add(AoCDirectory(dir, currentDir))
          } else {
            throw AssertionError()
          }
        }
        next[0] in '0'..'9' -> {
          val (size, name) = next.split(' ')
          if (!currentDir.files.any { it.name == name }) {
            currentDir.files.add(AoCFile(name, Integer.parseInt(size)))
          }
        }
      }
    }
    return root
  }

  data class AoCDirectory(val name: String, val parent: AoCDirectory?) {
    val children = mutableListOf<AoCDirectory>()
    val files = mutableListOf<AoCFile>()

    init {
      if (parent == null && name != "/") {
        throw AssertionError()
      }
    }

    fun dirSizes(): Map<AoCDirectory, Int> {
      return dirSizes(mutableMapOf())
    }

    private fun dirSizes(sizes: MutableMap<AoCDirectory, Int>): MutableMap<AoCDirectory, Int> {
      children.forEach {
        it.dirSizes(sizes)
      }
      sizes[this] = size()
      return sizes
    }

    fun size(): Int {
      return files.sumOf { it.size } + children.sumOf { it.size() }
    }

    fun path(): String {
      if (parent != null) {
        return if (parent.name == "/") {
          "$parent$name"
        } else {
          "$parent/$name"
        }
      }
      return name
    }

    override fun toString(): String {
      return path()
    }
  }

  data class AoCFile(val name: String, val size: Int)
}
