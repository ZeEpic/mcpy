package com.rimlang.rim.util

fun String.title()
    = this.split(" ")
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

fun String.snakeCase()
    = this.replace("(?<!^)[A-Z]".toRegex(), "_$0").lowercase()

fun String.classNameFromQualifiedName()
    = this.split(".").last()


fun String.camelCase(): String {
        val pascalCase = this.pascalCase()
        return pascalCase[0].lowercase() + pascalCase.drop(1)
}

fun String.pascalCase()
        = this.split("_").joinToString("") { it.title() }

val String.clazz
    get() = Class.forName(this)
