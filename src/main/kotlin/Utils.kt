import java.io.File

object Utils {
  private const val basePath = "src/main/kotlin";

  private fun getInputPath(clazz: Class<Any>): String {
    val pacakgeName = clazz.name.replace(".", "/").replace("/${clazz.simpleName}", "");
    return "$basePath/$pacakgeName"
  }

  fun readInput(clazz: Class<Any>, file: String): List<String> = readInput("${getInputPath(clazz)}/$file")
  private fun readInput(path: String): List<String> = File(path).readLines()
}
