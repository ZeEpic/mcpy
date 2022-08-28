package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.StringToken;

public class VariableDefinitionSyntaxNode extends SyntaxNode {
    private final StringToken identifier;
    private final GenericExpression initialValue;

    public VariableDefinitionSyntaxNode(StringToken identifier, GenericExpression initialValue) {
        this.identifier = identifier;
        this.initialValue = initialValue;
    }

    public StringToken getIdentifier() {
        return identifier;
    }

    public GenericExpression getInitialValue() {
        return initialValue;
    }
}
