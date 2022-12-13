package me.theos.aoc2022.challenges.day13

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.lang.AssertionError

class DistressSignal : Challenge {
  private val packets: MutableList<Elem> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    var inOrder = 0
    packets.chunked(2).withIndex().forEach {
      if (it.value[0] <= it.value[1]) {
        inOrder += it.index + 1
      }
    }
    return "Part 1: $inOrder"
  }

  override fun solveHard(): String {
    val div1 = parsePacket("[[2]]")
    val div2 = parsePacket("[[6]]")
    packets.add(div1)
    packets.add(div2)
    val decoderKey = packets
      .asSequence()
      .sorted()
      .withIndex()
      .filter { it.value == div1 || it.value == div2 }
      .fold(1) { acc, it -> acc * (it.index + 1) }
    packets.remove(div1)
    packets.remove(div2)
    return "Part 2: $decoderKey"
  }

  private fun parseInput(input: List<String>): MutableList<Elem> {
    return mutableListOf<Elem>().apply {
      input.filter(String::isNotEmpty).forEach {
        // nasty but ':' - '0' == 10
        add(parsePacket(it.replace("10", ":").replace(",", "")))
      }
    }
  }

  private fun parsePacket(input: String): Elem {
    return LElem(
      mutableListOf<Elem>().apply {
        var index = 1
        while (index < input.length - 1) {
          if (input[index] == '[') {
            var subPacketCount = 0
            subPacketCount++
            var subListIdx = index + 1
            while (subPacketCount != 0) {
              if (input[subListIdx] == '[') {
                subPacketCount++
              }
              if (input[subListIdx] == ']') {
                subPacketCount--
              }
              subListIdx++
            }
            add(parsePacket(input.substring(index, subListIdx)))
            index = subListIdx
          } else {
            if (input[index] in '0'..':') {
              add(IElem(input[index] - '0'))
            }
            index++
          }
        }
      }
    )
  }

  private data class IElem(val value: Int) : Elem {
    override fun toString(): String = "$value"
  }
  private data class LElem(val value: List<Elem>) : Elem {
    constructor(value: IElem) : this(listOf(value))
    override fun toString(): String = "$value"
  }
  private interface Elem : Comparable<Elem> {
    override operator fun compareTo(other: Elem): Int {
      return when {
        this is IElem && other is IElem -> {
          this.value.compareTo(other.value)
        }
        this is LElem && other is LElem -> {
          repeat(minOf(this.value.size, other.value.size)) { i ->
            val cmp = this.value[i].compareTo(other.value[i])
            if (cmp != 0) {
              return cmp
            }
          }
          this.value.size.compareTo(other.value.size)
        }
        else -> return when {
          this is IElem -> LElem(this).compareTo(other)
          other is IElem -> this.compareTo(LElem(other))
          else -> throw AssertionError()
        }
      }
    }
  }
}
