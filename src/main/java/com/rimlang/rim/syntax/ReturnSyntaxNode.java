package com.rimlang.rim.syntax;

public class ReturnSyntaxNode extends SyntaxNode {
    private GenericExpression genericExpression;

    public ReturnSyntaxNode(GenericExpression genericExpression) {
        this.genericExpression = genericExpression;
    }
}
