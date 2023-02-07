package com.mcpy.lang.lexer.token

import java.util.*


fun List<Token>.split(separator: TokenType, keepSplitToken: Boolean = false): List<List<Token>> {
    val result: MutableList<List<Token>> = ArrayList()
    var current: MutableList<Token> = ArrayList()
    for (t in this) {
        if (t.type === separator) {
            if (keepSplitToken) {
                current.add(t)
            }
            result.add(current)
            current = ArrayList()
        } else {
            current.add(t)
        }
    }
    if (current.isNotEmpty()) result.add(current)
    return result
}

fun nextToken(tokens: List<Token>, from: Int, type: TokenType, end: Int = tokens.size): Int {
    val stack = Stack<Int>()
    for (i in from until tokens.size) {
        val t = tokens[i]
        if (t.type !== type) continue
        if (t.value == type.values[0]) { // Open
            stack.push(i)
        } else if (t.value == type.values[1]) { // Close
            if (stack.isEmpty()) return i
            stack.pop()
        }
        if (i >= end) break
    }
    return -1
}

fun <E> sub(list: List<E>, from: Int, end: Int = list.size): List<E> {
    return ArrayList(list).subList(from, end)
}

fun getGroup(token: Token): List<Token> {
    return (token as GroupToken).value
}