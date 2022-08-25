package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.List;

public class CommandDefinitionSyntaxNode extends SyntaxNode {
    private final Token identifier;
    private final ArgsExpression args;
    private final List<SyntaxNode> body;

    public CommandDefinitionSyntaxNode(Token identifier, ArgsExpression args, List<SyntaxNode> body) {
        this.identifier = identifier;
        this.args = args;
        this.body = body;
    }
}
