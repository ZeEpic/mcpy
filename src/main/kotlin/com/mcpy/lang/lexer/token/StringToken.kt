package com.mcpy.lang.lexer.token

import com.mcpy.lang.errors.CodeFile

class StringToken(
    type: TokenType,
    override val value: String,
    line: Int,
    character: Int,
    file: CodeFile
) : Token(type, line, character, file)