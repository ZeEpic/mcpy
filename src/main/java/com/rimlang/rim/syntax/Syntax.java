package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public interface Syntax {

    /**
     * Checks if tokens match this syntax.
     * @param tokens the tokens to analyze.
     * @return true if tokens match this syntax, false otherwise.
     */
    boolean doesMatch(List<Token> tokens);

    /**
     * Translates this syntax to Java code.
     * @param tokens the tokens to parse.
     * @return the Java code.
     */
    String parse(List<Token> tokens);

}
