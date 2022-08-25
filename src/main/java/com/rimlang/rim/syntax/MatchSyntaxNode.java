package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;

import java.util.HashMap;
import java.util.List;

public class MatchSyntaxNode extends SyntaxNode {
    private GenericExpression genericExpression;
    private HashMap<Token, List<SyntaxNode>> branches;

    public MatchSyntaxNode(GenericExpression genericExpression, HashMap<Token, List<SyntaxNode>> branches) {
        this.genericExpression = genericExpression;
        this.branches = branches;
    }
    
}
