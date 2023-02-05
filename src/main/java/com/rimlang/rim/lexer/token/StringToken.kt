package com.rimlang.rim.lexer.token

import com.rimlang.rim.errors.CodeFile

class StringToken(
    type: TokenType,
    override val value: String,
    line: Int,
    character: Int,
    file: CodeFile
) : Token(type, line, character, file)