package com.rimlang.rim.lexer;

public class StringToken extends Token {
    private final String value;

    public StringToken(TokenType type, String value, int line, int character) {
        super(type, line, character);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object value() {
        return value;
    }
}
