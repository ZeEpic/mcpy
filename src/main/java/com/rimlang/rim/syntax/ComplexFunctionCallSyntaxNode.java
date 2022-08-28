package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.StringToken;
import com.rimlang.rim.lexer.Token;

import java.util.List;

public class ComplexFunctionCallSyntaxNode extends SyntaxNode {
    private final StringToken functionIdentifier;
    private final List<GenericExpression> args;
    private final List<SyntaxNode> body;

    public ComplexFunctionCallSyntaxNode(StringToken functionIdentifier, List<GenericExpression> args, List<SyntaxNode> body) {
        this.functionIdentifier = functionIdentifier;
        this.args = args;
        this.body = body;
    }

    public List<SyntaxNode> getBody() {
        return body;
    }

    public List<GenericExpression> getArgs() {
        return args;
    }

    public StringToken getFunctionIdentifier() {
        return functionIdentifier;
    }
}
