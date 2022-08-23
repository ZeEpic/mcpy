package com.rimlang.rim.lexer;

public record Token(TokenType type, String value, int line, int character) {

}