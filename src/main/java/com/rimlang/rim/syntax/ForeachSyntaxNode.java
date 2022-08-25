package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class ForeachSyntaxNode extends SyntaxNode {
    private final Token loopIdentifier;
    private final GenericExpression genericExpression;
    private final List<SyntaxNode> body;

    public ForeachSyntaxNode(Token loopIdentifier, GenericExpression genericExpression, List<SyntaxNode> body) {
        this.loopIdentifier = loopIdentifier;
        this.genericExpression = genericExpression;
        this.body = body;
    }
}
