package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class FunctionDefinitionSyntaxNode extends SyntaxNode {
    private final Token identifier;
    private final ArgsExpression args;
    private final Token returnType;
    private final List<SyntaxNode> body;

    public FunctionDefinitionSyntaxNode(Token identifier, ArgsExpression args, Token returnType, List<SyntaxNode> body) {
        this.identifier = identifier;
        this.args = args;
        this.returnType = returnType;
        this.body = body;
    }
}
