package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class BooleanExpression extends Expression {

    private final List<Token> tokens;

    public BooleanExpression(List<Token> tokens) {
        this.tokens = tokens;

    }

    private BooleanExpression parse(List<Token> tokens) {
        // a and (b and c)
        return null;
    }

    @Override
    public String translate() {
        return "true";//tokens.stream().map(t -> t.value().toString()).collect(Collectors.joining(" "));
    }
}
