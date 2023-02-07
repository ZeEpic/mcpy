package com.mcpy.lang.lexer.token

import com.mcpy.lang.errors.CodeFile

class NumberToken(
    type: TokenType,
    override val value: Double,
    line: Int,
    character: Int,
    file: CodeFile
) : Token(type, line, character, file)