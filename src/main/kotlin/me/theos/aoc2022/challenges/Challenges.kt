package me.theos.aoc2022.challenges

import java.io.File
import java.lang.AssertionError
import java.net.URL
import java.util.jar.JarFile

class Challenges {
  companion object {
    fun load(): List<Class<*>> {
      return ClassScanner.getClasses(Challenges::class.qualifiedName!!.substringBeforeLast('.'))
    }
  }
}

abstract class ClassScanner {
  companion object {
    private fun resource(resource: URL): ClassScanner {
      return when (resource.protocol) {
        "file" -> FsClassScanner(resource)
        "jar" -> JarClassScanner(resource)
        else -> throw AssertionError()
      }
    }

    fun getClasses(packageName: String): List<Class<*>> {
      val classLoader = Thread.currentThread().contextClassLoader
      val path = packageName.replace('.', '/')
      return arrayListOf<Class<*>>().apply {
        classLoader.getResources(path).asIterator().forEach {
          addAll(resource(it).getClasses(packageName))
        }
      }
    }
  }

  abstract fun getClasses(packageName: String): List<Class<*>>
}

class FsClassScanner(resource: URL) : ClassScanner() {
  private val file by lazy { File(resource.file) }

  override fun getClasses(packageName: String): List<Class<*>> {
    return arrayListOf<Class<*>>().apply {
      addAll(getClasses(file, packageName))
    }
  }

  private fun getClasses(directory: File, packageName: String): List<Class<*>> {
    if (!directory.exists()) {
      return listOf()
    }
    return arrayListOf<Class<*>>().apply {
      directory.listFiles()!!.forEach {
        if (it.isDirectory) {
          addAll(getClasses(it, packageName + "." + it.name))
        } else if (it.name.endsWith(".class")) {
          add(Class.forName(packageName + '.' + it.name.substring(0, it.name.length - 6)))
        }
      }
    }
  }
}

class JarClassScanner(resource: URL) : ClassScanner() {
  private val jar by lazy {
    val jar = resource.file
      .substringAfter(':')
      .substringBefore('!')
    JarFile(jar)
  }

  override fun getClasses(packageName: String): List<Class<*>> {
    val prefix = packageName.replace('.', '/')
    val entries = jar.entries()
    return arrayListOf<Class<*>>().apply {
      entries.iterator().forEach {
        if (it.isDirectory) {
          return@forEach
        } else if (it.name.endsWith(".class") && it.name.startsWith(prefix) && !it.name.contains("$")) {
          add(Class.forName(it.name.replace('/', '.').run { substring(0, length - 6) }))
        }
      }
    }
  }
}
