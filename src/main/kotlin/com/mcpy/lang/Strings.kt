package com.mcpy.lang

fun String.title()
    = this.split(" ")
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

fun String.enumNameTitle()
    = this.lowercase().replace("_", " ")
        .title()

fun String.snakeCase()
    = this.replace("(?<!^)[A-Z]".toRegex(), "_$0").lowercase()

fun String.classNameFromQualifiedName()
    = this.split(".").last()


fun String.camelCase(): String {
        val pascalCase = this.pascalCase()
        return pascalCase.replaceFirstChar { it.lowercase() }
}

fun String.pascalCase()
        = this.split("_").joinToString("") { it.title() }

val String.clazz: Class<*>
    get() = Class.forName(this)

fun String.asResource() = object {}.javaClass.getResource("/$this")!!.readText()

fun String.startsWithAny(vararg prefixes: String) = prefixes.any { this.startsWith(it) }
