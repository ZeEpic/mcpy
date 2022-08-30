package com.rimlang.rim.lexer

abstract class Token(val type: TokenType, val line: Int, val character: Int) {
    abstract val value: Any
    override fun toString(): String {
        return "{type: $type, value: $value, line: $line, char: $character}"
    }
}