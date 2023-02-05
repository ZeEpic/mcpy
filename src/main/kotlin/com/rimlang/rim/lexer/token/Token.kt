package com.rimlang.rim.lexer.token

import com.rimlang.rim.errors.CodeFile

abstract class Token(val type: TokenType, val line: Int, val character: Int, val file: CodeFile) {
    abstract val value: Any
    override fun toString(): String {
        return "{type: $type, value: $value, line: $line, char: $character}"
    }
}