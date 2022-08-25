package com.rimlang.rim.syntax;

import java.util.List;

public class WhileSyntaxNode extends SyntaxNode {
    private final BooleanExpression booleanExpression;
    private final List<SyntaxNode> body;

    public WhileSyntaxNode(BooleanExpression booleanExpression, List<SyntaxNode> body) {
        this.booleanExpression = booleanExpression;
        this.body = body;
    }
}
