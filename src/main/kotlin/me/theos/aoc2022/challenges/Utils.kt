package me.theos.aoc2022.challenges

import java.io.InputStream
import java.io.InputStreamReader

object Utils {
  fun getChallengeInput(clazz: Class<Any>): List<String> {
    val challengePackage = this.javaClass.name.substringBeforeLast('.')
    val challengeDay = clazz.name.removePrefix("$challengePackage.").substringBeforeLast('.')
    val input = this.javaClass.getResourceAsStream("/challengeInputs/$challengeDay.txt")
    return readInput(input!!)
  }

  private fun readInput(stream: InputStream): List<String> = InputStreamReader(stream).readLines()
}
