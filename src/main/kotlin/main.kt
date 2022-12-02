import challenges.Challenge
import java.io.File

fun main() {
  val challenges = getClasses("challenges")
  challenges.forEach {
    when (!it.isInterface && Challenge::class.java.isAssignableFrom(it)) {
      true -> {
        val challenge = it.getConstructor().newInstance()
        (challenge as Challenge).run {
          println(preamble())
          println("   ${solveEasy()}")
          println("   ${solveHard()}")
          println()
        }
      }
      false -> {}
    }
  }
}

fun getClasses(packageName: String): List<Class<*>> {
  val classLoader = Thread.currentThread().contextClassLoader
  val path = packageName.replace('.', '/')
  val resources = classLoader.getResources(path)
  val dirs = arrayListOf<File>()
  while (resources.hasMoreElements()) {
    val resource = resources.nextElement()
    dirs.add(File(resource.file))
  }
  val classes = arrayListOf<Class<*>>()
  dirs.forEach {
    classes.addAll(findClasses(it, packageName))
  }
  return classes
}

fun findClasses(directory: File, packageName: String): List<Class<*>> {
  val classes = arrayListOf<Class<*>>()
  if (!directory.exists()) {
    return classes
  }
  val files = directory.listFiles()
  files!!.forEach {
    if (it.isDirectory) {
      classes.addAll(findClasses(it, packageName + "." + it.name))
    } else if (it.name.endsWith(".class")) {
      classes.add(Class.forName(packageName + '.' + it.name.substring(0, it.name.length - 6)))
    }
  }
  return classes
}
