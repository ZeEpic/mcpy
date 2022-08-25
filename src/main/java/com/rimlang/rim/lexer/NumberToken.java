package com.rimlang.rim.lexer;

public class NumberToken extends Token {
    private final double value;

    public NumberToken(TokenType type, double value, int line, int character) {
        super(type, line, character);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public Object value() {
        return value;
    }
}
