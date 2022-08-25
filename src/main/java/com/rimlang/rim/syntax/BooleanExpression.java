package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class BooleanExpression extends Expression {

    private final List<Token> tokens;

    public BooleanExpression(List<Token> tokens) {
        this.tokens = tokens;
    }

}
