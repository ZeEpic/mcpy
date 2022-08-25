package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

public class VariableDefinitionSyntaxNode extends SyntaxNode {
    private final Token identifier;
    private final GenericExpression initialValue;

    public VariableDefinitionSyntaxNode(Token identifier, GenericExpression initialValue) {
        this.identifier = identifier;
        this.initialValue = initialValue;
    }
}
