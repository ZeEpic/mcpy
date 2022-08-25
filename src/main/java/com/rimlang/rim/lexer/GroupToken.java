package com.rimlang.rim.lexer;

import java.util.List;

public class GroupToken extends Token {
    private final List<Token> value;

    public GroupToken(TokenType type, List<Token> value, int line, int character) {
        super(type, line, character);
        this.value = value;
    }

    public List<Token> getValue() {
        return value;
    }

    @Override
    public Object value() {
        return value;
    }
}
