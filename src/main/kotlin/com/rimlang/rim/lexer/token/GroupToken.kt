package com.rimlang.rim.lexer.token

import com.rimlang.rim.errors.CodeFile

class GroupToken(
    type: TokenType,
    override val value: List<Token>,
    line: Int,
    character: Int,
    file: CodeFile
) : Token(type, line, character, file)