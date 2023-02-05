package com.rimlang.rim.lexer.token

import com.rimlang.rim.errors.CodeFile

class NumberToken(
    type: TokenType,
    override val value: Double,
    line: Int,
    character: Int,
    file: CodeFile
) : Token(type, line, character, file)