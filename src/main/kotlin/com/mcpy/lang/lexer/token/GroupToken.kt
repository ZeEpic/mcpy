package com.mcpy.lang.lexer.token

import com.mcpy.lang.errors.CodeFile

class GroupToken(
    type: TokenType,
    override val value: List<Token>,
    line: Int,
    character: Int,
    file: CodeFile
) : Token(type, line, character, file)