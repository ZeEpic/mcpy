package com.rimlang.rim.lexer

class GroupToken(
    type: TokenType,
    override val value: List<Token>,
    line: Int,
    character: Int
) : Token(type, line, character)