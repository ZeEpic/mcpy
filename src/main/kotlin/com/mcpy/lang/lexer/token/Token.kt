package com.mcpy.lang.lexer.token

import com.mcpy.lang.errors.CodeFile

abstract class Token(val type: TokenType, val line: Int, val character: Int, val file: CodeFile) {
    abstract val value: Any
    override fun toString(): String {
        return "{type: $type, value: $value, line: $line, char: $character}"
    }
}

fun <E, T> List<E>.split(separator: T, keepSplitValue: Boolean = false, splitBy: (E) -> T): List<List<E>> {
    val result = mutableListOf<List<E>>()
    val current = mutableListOf<E>()
    for (item in this) {
        if (splitBy(item) == separator) {
            if (keepSplitValue) {
                current.add(item)
            }
            result.add(current)
            current.clear()
            continue
        }
        current.add(item)
    }
    if (current.isNotEmpty()) result.add(current)
    return result
}

fun <E> List<List<E>>.join(separator: (E) -> E): List<E> {
    val result = mutableListOf<E>()
    for (i in this.indices) {
        val list = this[i]
        result.addAll(list)
        if (list.isNotEmpty() && i != this.lastIndex) {
            result += separator(list.last())
        }
    }
    return result
}

fun List<Token>.countRecursive(type: TokenType? = null): Int {
    var count = 0
    for (i in this.indices) {
        val token = this[i]
        if (token.type == type || type == null) count++
        val isArgumentGroup = this.getOrNull(i - 1)?.type == TokenType.ID
        if (token is GroupToken && !isArgumentGroup) {
            count += token.value.countRecursive(type)
        }
    }
    return count
}
