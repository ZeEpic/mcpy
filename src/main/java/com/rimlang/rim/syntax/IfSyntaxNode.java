package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.HashMap;
import java.util.List;

public class IfSyntaxNode extends SyntaxNode {
    private final BooleanExpression booleanExpression;
    private final List<SyntaxNode> body;

    public IfSyntaxNode(BooleanExpression booleanExpression, List<SyntaxNode> body) {
        this.booleanExpression = booleanExpression;
        this.body = body;
    }

    public BooleanExpression getBooleanExpression() {
        return booleanExpression;
    }

    public List<SyntaxNode> getBody() {
        return body;
    }
}
