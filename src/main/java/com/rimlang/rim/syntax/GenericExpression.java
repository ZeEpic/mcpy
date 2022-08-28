package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class GenericExpression extends Expression {
    private final List<Token> tokens;

    public GenericExpression(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String translate() {
        return null;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getResultType() {
        return "null";
    }
}
