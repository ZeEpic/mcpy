package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.StringToken;

import java.util.List;

public class CommandDefinitionSyntaxNode extends SyntaxNode {
    private final StringToken identifier;
    private final ArgsExpression args;
    private final List<SyntaxNode> body;

    public CommandDefinitionSyntaxNode(StringToken identifier, ArgsExpression args, List<SyntaxNode> body) {
        this.identifier = identifier;
        this.args = args;
        this.body = body;
    }

    public List<SyntaxNode> getBody() {
        return body;
    }

    public ArgsExpression getArgs() {
        return args;
    }

    public StringToken getIdentifier() {
        return identifier;
    }
}
