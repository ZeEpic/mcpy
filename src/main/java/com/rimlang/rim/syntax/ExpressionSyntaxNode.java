package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class ExpressionSyntaxNode extends SyntaxNode {
    private final List<Token> code;

    public ExpressionSyntaxNode(List<Token> code) {
        this.code = code;
    }

    public List<Token> getCode() {
        return code;
    }

}
