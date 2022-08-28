package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.StringToken;

import java.util.List;

public class FunctionDefinitionSyntaxNode extends SyntaxNode {
    private final StringToken identifier;
    private final ArgsExpression args;
    private final StringToken returnType;
    private final List<SyntaxNode> body;

    public FunctionDefinitionSyntaxNode(StringToken identifier, ArgsExpression args, StringToken returnType, List<SyntaxNode> body) {
        this.identifier = identifier;
        this.args = args;
        this.returnType = returnType;
        this.body = body;
    }

    public List<SyntaxNode> getBody() {
        return body;
    }

    public StringToken getReturnType() {
        return returnType;
    }

    public ArgsExpression getArgs() {
        return args;
    }

    public StringToken getIdentifier() {
        return identifier;
    }
}
