package com.rimlang.rim.lexer;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class Token {
    private final TokenType type;
    private final int line;
    private final int character;

    public Token(TokenType type, int line, int character) {
        this.type = type;
        this.line = line;
        this.character = character;
    }

    public abstract Object value();

    public TokenType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return "{type: " + type + ", value: " + value() + ", line: " + line + ", char: " + character + "}";
    }
}