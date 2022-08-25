package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class GenericExpression {
    private final List<Token> tokens;

    public GenericExpression(List<Token> tokens) {
        this.tokens = tokens;
    }
}
