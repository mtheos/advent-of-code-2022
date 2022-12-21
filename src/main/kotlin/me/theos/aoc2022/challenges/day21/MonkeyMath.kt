package me.theos.aoc2022.challenges.day21

import me.theos.aoc2022.challenges.Challenge
import me.theos.aoc2022.challenges.Utils
import java.lang.AssertionError

class MonkeyMath : Challenge {
  private val root = "root"
  private val humn = "humn"
  private val context: Map<String, List<String>> by lazy {
    val input = Utils.getChallengeInput(this.javaClass)
    parseInput(input)
  }

  override fun solveEasy(): String {
    val monkeyChain = buildExpr(root)
    val result = monkeyChain.resolve() as Const
    return "Part 1: ${result.value}"
  }

  override fun solveHard(): String {
    val rootOp = context[root]!![1]
    val humnVal = context[humn]!![0]
    (context[root]!! as MutableList)[1] = "="
    (context[humn]!! as MutableList)[0] = "var"
    var monkeyChain = buildExpr(root) as Equality
    while (monkeyChain.left !is Variable) {
      monkeyChain = invert(monkeyChain)
    }
    val result = monkeyChain.right as Const
    (context[root]!! as MutableList)[1] = rootOp
    (context[humn]!! as MutableList)[0] = humnVal
    return "Part 2: ${result.value}"
  }

  private fun invert(expr: Equality): Equality {
    when (expr.left) {
      is Equation -> {
        when (expr.left.op) {
          "+" -> {
            when (expr.left.left) {
              is Equation -> {
                return Equality(expr.left.left, Equation(expr.right, expr.left.right, "-").resolve())
              }
              is Const -> {
                return Equality(expr.left.right, Equation(expr.right, expr.left.left, "-").resolve())
              }
              is Variable -> {
                return Equality(expr.left.right, Equation(expr.right, expr.left.left, "-").resolve())
              }
            }
          }
          "*" -> {
            when (expr.left.left) {
              is Equation -> {
                return Equality(expr.left.left, Equation(expr.right, expr.left.right, "/").resolve())
              }
              is Const -> {
                return Equality(expr.left.right, Equation(expr.right, expr.left.left, "/").resolve())
              }
              is Variable -> {
                return Equality(expr.left.right, Equation(expr.right, expr.left.left, "/").resolve())
              }
            }
          }
          "-" -> {
            return Equality(expr.left.left, Equation(expr.right, expr.left.right, "+").resolve())
          }
          "/" -> {
            return Equality(expr.left.left, Equation(expr.right, expr.left.right, "*").resolve())
          }
        }
      }
      is Const -> {
        return Equality(expr.right, expr.left)
      }
    }
    return expr
  }


  private fun parseInput(input: List<String>): Map<String, List<String>> {
    return mutableMapOf<String, List<String>>().apply {
      input.forEach {
        val (name, expr) = it.split(": ")
        val parts = expr.split(" ").toMutableList()
        put(name, parts)
      }
    }
  }

  private fun buildExpr(name: String): Expr {
    val parts = context[name]!!
    return if (parts.size > 1) {
      when (parts[1]) {
        "=" -> Equality(buildExpr(parts[0]), buildExpr(parts[2]))
        else -> Equation(buildExpr(parts[0]), buildExpr(parts[2]), parts[1])
      }
    } else if (parts[0] == "var") {
      Variable()
    } else {
      Const(parts[0].toLong())
    }
  }

  private interface Expr {
    fun resolve(): Expr
  }

  private data class Const(val value: Long) : Expr {
    override fun resolve(): Expr = this
  }

  private data class Variable(private val unused: Long = 0) : Expr {
    override fun resolve(): Expr = this
  }

  private data class Equality(val left: Expr, val right: Expr) : Expr {
    override fun resolve(): Expr = Equality(left.resolve(), right.resolve())
  }

  private data class Equation(val left: Expr, val right: Expr, val op: String) : Expr {
    override fun resolve(): Expr {
      val l = left.resolve()
      val r = right.resolve()
      if (l !is Const || r !is Const) {
        return Equation(l, r, op)
      }
      return when (op) {
        "+" -> Const(l.value + r.value)
        "-" -> Const(l.value - r.value)
        "*" -> Const(l.value * r.value)
        "/" -> Const(l.value / r.value)
        else -> throw AssertionError()
      }
    }
  }
}
